package com.rebottle.app.model

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.rebottle.app.logica.PuntoRuta
import com.rebottle.app.logica.calcularTiemposParadas
import com.rebottle.app.logica.paradasVencidas
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date
import java.util.Locale


// ============================================================
// MODELOS
// ============================================================

enum class EstadoParada {
    PENDIENTE,
    COMPLETADA,
    CANCELADA
}

data class ParadaRuta(
    val orden: Int,
    val solicitudId: String,
    val direccion: String,
    val nombreLugar: String,
    val materiales: List<String>,
    val pesoKg: Int,
    val latitud: Double,
    val longitud: Double,
    val estado: EstadoParada,
    val completadaEn: Timestamp?
)

data class RutaProgresoState(
    val ruta: Ruta? = null,
    val paradas: List<ParadaRuta> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val resolviendoId: String? = null,
    val resolucionError: String? = null
) {
    val total: Int get() = paradas.size
    val completadas: Int get() = paradas.count { it.estado == EstadoParada.COMPLETADA }
    val pendientes: Int get() = paradas.count { it.estado == EstadoParada.PENDIENTE }
}


// ============================================================
// VIEWMODEL
//
//   primera parada resuelta  -> ruta EN_CURSO
//   última parada resuelta   -> ruta COMPLETADA y el recolector
//                               vuelve a DISPONIBLE
//
// Parada completada = solicitud RECOLECTADA + completadaEn
//
// Progreso automático: desde la hora de salida, las paradas se
// completan solas según el tiempo estimado caminando desde la
// fundación y entre paradas, más 5 a 7 min en cada una
// (ver logica/ProgresoRutaLogica.kt).
// ============================================================

// Cada cuánto se revisa si ya terminó alguna parada
private const val INTERVALO_REVISION_MS = 30_000L

class RutaProgresoViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val firestore = FirebaseFirestore.getInstance()

    // ---------- Progreso automático ----------

    // Dirección de la fundación (punto de partida de la ruta)
    private var origenFundacion: PuntoRuta? = null
    private var origenCargado = false
    private var cargandoOrigen = false

    // Hora en que se asignó la ruta (se usa si no hay hora de salida)
    private var asignadaEnMillis: Long? = null

    // Paradas que este celular ya marcó, para no repetirlas
    private val autoCompletadas = mutableSetOf<String>()

    private var revisionJob: Job? = null

    private val _state = MutableStateFlow(RutaProgresoState())
    val state: StateFlow<RutaProgresoState> = _state.asStateFlow()

    private var rutaListener: ListenerRegistration? = null
    private var solicitudesListener: ListenerRegistration? = null

    private var recolectorEscuchado: String? = null
    private var rutaActual: Ruta? = null
    private var idsEscuchados: List<String> = emptyList()
    private var solicitudesDocs: List<DocumentSnapshot> = emptyList()


    fun escucharRutaDe(recolectorId: String) {

        if (recolectorId.isBlank() || recolectorId == recolectorEscuchado) return

        detener()
        recolectorEscuchado = recolectorId
        _state.value = RutaProgresoState(isLoading = true)

        rutaListener =
            firestore
                .collection("rutas")
                .whereEqualTo("recolectorId", recolectorId)
                .whereIn(
                    "estado",
                    listOf(
                        EstadoRuta.ASIGNADA.name,
                        EstadoRuta.EN_CURSO.name,
                        EstadoRuta.COMPLETADA.name
                    )
                )
                .addSnapshotListener { snapshot, exception ->

                    if (exception != null) {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al cargar la ruta"
                        )
                        return@addSnapshotListener
                    }

                    val ahora = System.currentTimeMillis()
                    val ventana = 24L * 60L * 60L * 1000L

                    // Activa primero; una ruta terminada se muestra 24 h
                    val rutaDoc =
                        snapshot?.documents
                            ?.filter { doc ->
                                doc.getString("estado") != EstadoRuta.COMPLETADA.name ||
                                        (doc.getTimestamp("completadaEn")?.toDate()?.time ?: 0L) >= ahora - ventana
                            }
                            ?.sortedWith(
                                compareBy<DocumentSnapshot> {
                                    it.getString("estado") == EstadoRuta.COMPLETADA.name
                                }.thenByDescending {
                                    it.getTimestamp("creadaEn")?.seconds ?: 0L
                                }
                            )
                            ?.firstOrNull()

                    val ruta = rutaDoc?.let { aRuta(it) }

                    asignadaEnMillis = rutaDoc?.getTimestamp("asignadaEn")?.toDate()?.time

                    if (ruta == null) {
                        solicitudesListener?.remove()
                        solicitudesListener = null
                        rutaActual = null
                        idsEscuchados = emptyList()
                        solicitudesDocs = emptyList()
                        _state.value = RutaProgresoState(isLoading = false)
                        return@addSnapshotListener
                    }

                    rutaActual = ruta

                    val ids = ruta.ordenSolicitudes.ifEmpty { ruta.solicitudesIds }

                    if (ids != idsEscuchados) {
                        idsEscuchados = ids
                        escucharSolicitudes(ids)
                    } else {
                        publicar()
                    }
                }
    }


    private fun escucharSolicitudes(ids: List<String>) {

        solicitudesListener?.remove()
        solicitudesListener = null
        solicitudesDocs = emptyList()

        if (ids.isEmpty()) {
            publicar()
            return
        }

        // Firestore permite como máximo 30 valores en whereIn
        solicitudesListener =
            firestore
                .collection("solicitudes")
                .whereIn(FieldPath.documentId(), ids.take(30))
                .addSnapshotListener { snapshot, exception ->

                    if (exception != null) {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al cargar las paradas"
                        )
                        return@addSnapshotListener
                    }

                    solicitudesDocs = snapshot?.documents.orEmpty()
                    publicar()
                }
    }


    private fun publicar() {

        val ruta = rutaActual ?: return

        val ids = ruta.ordenSolicitudes.ifEmpty { ruta.solicitudesIds }
        val porId = solicitudesDocs.associateBy { it.id }

        val paradas =
            ids.mapIndexedNotNull { index, id ->
                porId[id]?.let { aParada(index + 1, it) }
            }

        _state.value = _state.value.copy(
            ruta = ruta,
            paradas = paradas,
            isLoading = ids.isNotEmpty() && solicitudesDocs.isEmpty(),
            error = null
        )

        iniciarRevisionAutomatica()
        revisarProgresoAutomatico()
    }


    // ============================================================
    // PROGRESO AUTOMÁTICO
    // ============================================================

    private fun iniciarRevisionAutomatica() {

        if (revisionJob?.isActive == true) return

        revisionJob =
            viewModelScope.launch {
                while (isActive) {
                    delay(INTERVALO_REVISION_MS)
                    revisarProgresoAutomatico()
                }
            }
    }

    /**
     * Hora de salida de la ruta: fecha de recolección + hora que definió
     * la fundación. Si no hay hora, se usa el momento en que se asignó.
     */
    private fun inicioRutaMillis(ruta: Ruta): Long? {

        val fecha = runCatching { LocalDate.parse(ruta.fechaRecoleccion) }.getOrNull()

        val partes = ruta.hora.split(":")
        val hora = partes.getOrNull(0)?.trim()?.toIntOrNull()
        val minuto = partes.getOrNull(1)?.trim()?.toIntOrNull()

        if (fecha != null && hora != null && minuto != null && hora in 0..23 && minuto in 0..59) {
            return fecha
                .atTime(LocalTime.of(hora, minuto))
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        }

        return asignadaEnMillis
    }

    private fun revisarProgresoAutomatico() {

        val actual = _state.value
        val ruta = actual.ruta ?: return

        if (
            ruta.estado != EstadoRuta.ASIGNADA.name &&
            ruta.estado != EstadoRuta.EN_CURSO.name
        ) return

        if (actual.resolviendoId != null || actual.paradas.isEmpty()) return

        val inicio = inicioRutaMillis(ruta) ?: return

        // Antes de la hora de salida no hay nada que revisar
        if (System.currentTimeMillis() < inicio) return

        if (!origenCargado) {
            cargarOrigenFundacion()
            return
        }

        val tiempos =
            calcularTiemposParadas(
                inicioMillis = inicio,
                origen = origenFundacion,
                paradas = actual.paradas.map {
                    PuntoRuta(it.solicitudId, it.latitud, it.longitud)
                }
            )

        val pendientes =
            actual.paradas
                .filter { it.estado == EstadoParada.PENDIENTE && it.solicitudId !in autoCompletadas }
                .map { it.solicitudId }
                .toSet()

        // Se completa una a la vez, en orden; al terminar se revisa la siguiente
        val siguiente =
            paradasVencidas(tiempos, pendientes, System.currentTimeMillis())
                .firstOrNull() ?: return

        autoCompletadas += siguiente.solicitudId

        resolverParada(
            solicitudId = siguiente.solicitudId,
            completada = true,
            completadaEn = Timestamp(Date(siguiente.finMillis)),
            silencioso = true,
            onSuccess = { revisarProgresoAutomatico() }
        )
    }

    /**
     * Busca la dirección de la fundación y la convierte en coordenadas.
     * Si no se encuentra, la ruta empieza a contar desde la primera parada.
     */
    private fun cargarOrigenFundacion() {

        if (cargandoOrigen) return
        cargandoOrigen = true

        firestore
            .collection("users")
            .whereEqualTo("role", "fundacion")
            .limit(1)
            .get()
            .addOnSuccessListener { snapshot ->

                val doc = snapshot.documents.firstOrNull()

                val lat = doc?.getDouble("latitud")
                val lon = doc?.getDouble("longitud")

                if (lat != null && lon != null && (lat != 0.0 || lon != 0.0)) {
                    terminarCargaOrigen(PuntoRuta("fundacion", lat, lon))
                    return@addOnSuccessListener
                }

                val direccion = doc?.getString("direccion")?.trim().orEmpty()

                if (direccion.isEmpty()) {
                    terminarCargaOrigen(null)
                    return@addOnSuccessListener
                }

                viewModelScope.launch {
                    terminarCargaOrigen(geocodificar(direccion))
                }
            }
            .addOnFailureListener {
                terminarCargaOrigen(null)
            }
    }

    private fun terminarCargaOrigen(origen: PuntoRuta?) {
        origenFundacion = origen
        origenCargado = true
        cargandoOrigen = false
        revisarProgresoAutomatico()
    }

    @Suppress("DEPRECATION")
    private suspend fun geocodificar(direccion: String): PuntoRuta? =
        withContext(Dispatchers.IO) {
            runCatching {
                Geocoder(getApplication(), Locale("es", "CO"))
                    .getFromLocationName(direccion, 1)
                    ?.firstOrNull()
                    ?.let { PuntoRuta("fundacion", it.latitude, it.longitude) }
            }.getOrNull()
        }


    fun completarParada(solicitudId: String, onSuccess: () -> Unit = {}) =
        resolverParada(solicitudId, completada = true, onSuccess = onSuccess)

    fun cancelarParada(solicitudId: String, onSuccess: () -> Unit = {}) =
        resolverParada(solicitudId, completada = false, onSuccess = onSuccess)

    /**
     * @param completadaEn hora de recolección; null = hora del servidor
     * @param silencioso no muestra errores (lo usa el progreso automático)
     */
    private fun resolverParada(
        solicitudId: String,
        completada: Boolean,
        onSuccess: () -> Unit,
        completadaEn: Timestamp? = null,
        silencioso: Boolean = false
    ) {

        if (_state.value.resolviendoId != null) return

        val ruta = _state.value.ruta

        if (ruta == null) {
            if (!silencioso) {
                _state.value = _state.value.copy(resolucionError = "No hay una ruta activa")
            }
            return
        }

        val ids = ruta.ordenSolicitudes.ifEmpty { ruta.solicitudesIds }

        if (solicitudId !in ids) {
            if (!silencioso) {
                _state.value = _state.value.copy(resolucionError = "La parada no pertenece a esta ruta")
            }
            return
        }

        _state.value = _state.value.copy(
            resolviendoId = solicitudId,
            resolucionError = null
        )

        val rutaRef = firestore.collection("rutas").document(ruta.id)

        firestore
            .runTransaction { transaction ->

                // ---------- LECTURAS ----------

                val rutaSnap = transaction.get(rutaRef)

                if (!rutaSnap.exists()) {
                    throw IllegalStateException("La ruta ya no existe")
                }

                val estadoRuta = rutaSnap.getString("estado") ?: ""

                if (
                    estadoRuta != EstadoRuta.ASIGNADA.name &&
                    estadoRuta != EstadoRuta.EN_CURSO.name
                ) {
                    throw IllegalStateException("La ruta ya no está activa")
                }

                val recolectorId = rutaSnap.getString("recolectorId")

                val solicitudes =
                    ids.map { id ->
                        transaction.get(firestore.collection("solicitudes").document(id))
                    }

                val objetivo = solicitudes.firstOrNull { it.id == solicitudId }

                if (objetivo == null || !objetivo.exists()) {
                    throw IllegalStateException("La solicitud ya no existe")
                }

                val resueltos = listOf(
                    EstadoSolicitud.RECOLECTADA.name,
                    EstadoSolicitud.CANCELADA.name
                )

                if (objetivo.getString("estado") in resueltos) {
                    throw IllegalStateException("Esta parada ya fue resuelta")
                }

                val pendientesDespues =
                    solicitudes.count {
                        it.id != solicitudId && it.getString("estado") !in resueltos
                    }

                // ---------- ESCRITURAS ----------

                val cambiosSolicitud = mutableMapOf<String, Any>(
                    "estado" to
                            if (completada) EstadoSolicitud.RECOLECTADA.name
                            else EstadoSolicitud.CANCELADA.name
                )

                if (completada) {
                    cambiosSolicitud["completadaEn"] = completadaEn ?: FieldValue.serverTimestamp()
                }

                if (completadaEn != null) {
                    // Queda registrado que la marcó el progreso automático
                    cambiosSolicitud["completadaAutomaticamente"] = true
                }

                transaction.update(objetivo.reference, cambiosSolicitud)

                if (pendientesDespues == 0) {

                    transaction.update(
                        rutaRef,
                        mapOf(
                            "estado" to EstadoRuta.COMPLETADA.name,
                            "completadaEn" to FieldValue.serverTimestamp()
                        )
                    )

                    if (!recolectorId.isNullOrBlank()) {
                        transaction.update(
                            firestore.collection("users").document(recolectorId),
                            "estado",
                            "DISPONIBLE"
                        )
                    }

                } else if (estadoRuta == EstadoRuta.ASIGNADA.name) {

                    transaction.update(rutaRef, "estado", EstadoRuta.EN_CURSO.name)
                }

                true
            }
            .addOnSuccessListener {
                _state.value = _state.value.copy(resolviendoId = null)
                onSuccess()
            }
            .addOnFailureListener { exception ->
                // Si falló (por ejemplo sin internet), se reintenta en la próxima revisión
                if (silencioso) autoCompletadas.remove(solicitudId)

                _state.value = _state.value.copy(
                    resolviendoId = null,
                    resolucionError =
                        if (silencioso) null
                        else exception.message ?: "No se pudo actualizar la parada"
                )
            }
    }

    fun limpiarResolucionError() {
        _state.value = _state.value.copy(resolucionError = null)
    }


    private fun aRuta(doc: DocumentSnapshot): Ruta {

        fun lista(campo: String): List<String> =
            (doc.get(campo) as? List<*>)?.mapNotNull { it as? String }.orEmpty()

        return Ruta(
            id = doc.id,
            solicitudesIds = lista("solicitudesIds"),
            fechaRecoleccion = doc.getString("fechaRecoleccion") ?: "",
            hora = doc.getString("hora") ?: "",
            estado = doc.getString("estado") ?: EstadoRuta.PUBLICADA.name,
            recolectorId = doc.getString("recolectorId"),
            ordenSolicitudes = lista("ordenSolicitudes"),
            distanciaTotalMetros = doc.getLong("distanciaTotalMetros") ?: 0L,
            duracionTotalSegundos = doc.getLong("duracionTotalSegundos") ?: 0L,
            polyline = doc.getString("polyline") ?: "",
            creadaEn = doc.getTimestamp("creadaEn")
        )
    }

    private fun aParada(orden: Int, doc: DocumentSnapshot): ParadaRuta {

        val estado =
            when (doc.getString("estado")) {
                EstadoSolicitud.RECOLECTADA.name -> EstadoParada.COMPLETADA
                EstadoSolicitud.CANCELADA.name -> EstadoParada.CANCELADA
                else -> EstadoParada.PENDIENTE
            }

        return ParadaRuta(
            orden = orden,
            solicitudId = doc.id,
            direccion = doc.getString("direccion") ?: "",
            nombreLugar = doc.getString("nombreLugar") ?: "",
            materiales = (doc.get("materiales") as? List<*>)
                ?.mapNotNull { it as? String }.orEmpty(),
            pesoKg = doc.getLong("pesoKg")?.toInt() ?: 0,
            latitud = doc.getDouble("latitud") ?: 0.0,
            longitud = doc.getDouble("longitud") ?: 0.0,
            estado = estado,
            completadaEn = doc.getTimestamp("completadaEn")
        )
    }


    fun detener() {
        rutaListener?.remove()
        solicitudesListener?.remove()
        rutaListener = null
        solicitudesListener = null
        recolectorEscuchado = null
        rutaActual = null
        idsEscuchados = emptyList()
        solicitudesDocs = emptyList()
        revisionJob?.cancel()
        revisionJob = null
        asignadaEnMillis = null
        autoCompletadas.clear()
    }

    override fun onCleared() {
        detener()
        super.onCleared()
    }
}