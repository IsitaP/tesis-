package com.rebottle.app.model

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


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
// ============================================================

class RutaProgresoViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

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
                    val ruta =
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
                            ?.let { aRuta(it) }

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
    }


    fun completarParada(solicitudId: String, onSuccess: () -> Unit = {}) =
        resolverParada(solicitudId, completada = true, onSuccess = onSuccess)

    fun cancelarParada(solicitudId: String, onSuccess: () -> Unit = {}) =
        resolverParada(solicitudId, completada = false, onSuccess = onSuccess)

    private fun resolverParada(
        solicitudId: String,
        completada: Boolean,
        onSuccess: () -> Unit
    ) {

        if (_state.value.resolviendoId != null) return

        val ruta = _state.value.ruta

        if (ruta == null) {
            _state.value = _state.value.copy(resolucionError = "No hay una ruta activa")
            return
        }

        val ids = ruta.ordenSolicitudes.ifEmpty { ruta.solicitudesIds }

        if (solicitudId !in ids) {
            _state.value = _state.value.copy(resolucionError = "La parada no pertenece a esta ruta")
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
                    cambiosSolicitud["completadaEn"] = FieldValue.serverTimestamp()
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
                _state.value = _state.value.copy(
                    resolviendoId = null,
                    resolucionError = exception.message ?: "No se pudo actualizar la parada"
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
    }

    override fun onCleared() {
        detener()
        super.onCleared()
    }
}