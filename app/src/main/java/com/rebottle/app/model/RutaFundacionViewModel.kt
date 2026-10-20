package com.rebottle.app.model

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.rebottle.app.ui.screens.auth.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


// ============================================================
// MODELOS
// ============================================================

enum class EstadoRecolectorVista(val etiqueta: String, val prioridad: Int) {
    TERMINANDO("Terminando", 0),
    EN_RUTA("En ruta", 1),
    ASIGNADA("Asignada", 2),
    DISPONIBLE("Disponible", 3),
    NO_DISPONIBLE("No disponible", 4)
}

data class RecolectorRuta(
    val uid: String,
    val nombre: String,
    val fotoUrl: String,
    val localidad: String,
    val estado: EstadoRecolectorVista,
    val pendientes: Int,
    val rutaId: String?
)

data class PuntoRuta(
    val solicitudId: String,
    val recolectorId: String,
    val latitud: Double,
    val longitud: Double,
    val estado: EstadoParada
)

data class RutasFundacionState(
    val recolectores: List<RecolectorRuta> = emptyList(),
    val puntos: List<PuntoRuta> = emptyList(),
    val disponibles: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)


// ============================================================
// VIEWMODEL
//
//   users (role = recolector)      -> nombre, foto, estado, localidad
//   rutas (ASIGNADA / EN_CURSO)    -> ruta de cada recolector
//   solicitudes de esas rutas      -> pendientes y ubicaciones
//
// "Terminando" = ruta EN_CURSO con una sola parada pendiente.
// ============================================================

class RutasFundacionViewModel : ViewModel() {

    private val firestore = FirebaseFirestore.getInstance()

    private val _state = MutableStateFlow(RutasFundacionState())
    val state: StateFlow<RutasFundacionState> = _state.asStateFlow()

    private var usuariosListener: ListenerRegistration? = null
    private var rutasListener: ListenerRegistration? = null
    private var solicitudesListeners: List<ListenerRegistration> = emptyList()

    private var recolectoresDocs: List<DocumentSnapshot> = emptyList()
    private var rutasDocs: List<DocumentSnapshot> = emptyList()
    private val solicitudesPorGrupo = mutableMapOf<Int, List<DocumentSnapshot>>()
    private var idsEscuchados: List<String> = emptyList()

    private var usuariosListos = false
    private var rutasListas = false


    fun escuchar() {

        if (usuariosListener != null) return

        usuariosListener =
            firestore
                .collection("users")
                .whereEqualTo("role", UserRole.RECOLECTOR.firestoreValue)
                .addSnapshotListener { snapshot, exception ->

                    if (exception != null) {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al cargar recolectores"
                        )
                        return@addSnapshotListener
                    }

                    recolectoresDocs = snapshot?.documents.orEmpty()
                    usuariosListos = true
                    recalcular()
                }

        rutasListener =
            firestore
                .collection("rutas")
                .whereIn(
                    "estado",
                    listOf(
                        EstadoRuta.ASIGNADA.name,
                        EstadoRuta.EN_CURSO.name
                    )
                )
                .addSnapshotListener { snapshot, exception ->

                    if (exception != null) {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Error al cargar las rutas"
                        )
                        return@addSnapshotListener
                    }

                    rutasDocs = snapshot?.documents.orEmpty()
                    rutasListas = true
                    reconectarSolicitudes()
                    recalcular()
                }
    }


    // Firestore limita whereIn a 30 valores, por eso se agrupan
    private fun reconectarSolicitudes() {

        val ids = rutasDocs.flatMap { idsDe(it) }.distinct()

        if (ids == idsEscuchados) return

        idsEscuchados = ids
        solicitudesListeners.forEach { it.remove() }
        solicitudesPorGrupo.clear()

        solicitudesListeners =
            ids.chunked(30).mapIndexed { indice, grupo ->

                firestore
                    .collection("solicitudes")
                    .whereIn(FieldPath.documentId(), grupo)
                    .addSnapshotListener { snapshot, exception ->

                        if (exception != null) {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                error = exception.message ?: "Error al cargar las paradas"
                            )
                            return@addSnapshotListener
                        }

                        solicitudesPorGrupo[indice] = snapshot?.documents.orEmpty()
                        recalcular()
                    }
            }
    }


    private fun recalcular() {

        if (!usuariosListos || !rutasListas) return

        val solicitudes =
            solicitudesPorGrupo.values.flatten().associateBy { it.id }

        val resueltos = listOf(
            EstadoSolicitud.RECOLECTADA.name,
            EstadoSolicitud.CANCELADA.name
        )

        val rutaPorRecolector: Map<String, DocumentSnapshot> =
            rutasDocs
                .filter { !it.getString("recolectorId").isNullOrBlank() }
                .groupBy { it.getString("recolectorId")!! }
                .mapValues { (_, docs) ->
                    docs.maxByOrNull { it.getTimestamp("creadaEn")?.seconds ?: 0L }!!
                }

        val recolectores =
            recolectoresDocs
                .map { doc ->

                    val ruta = rutaPorRecolector[doc.id]
                    val ids = ruta?.let { idsDe(it) }.orEmpty()

                    val pendientes =
                        ids.count { solicitudes[it]?.getString("estado") !in resueltos }

                    val estadoRuta = ruta?.getString("estado")

                    val vista =
                        when {
                            ruta != null &&
                                    estadoRuta == EstadoRuta.EN_CURSO.name &&
                                    pendientes <= 1 -> EstadoRecolectorVista.TERMINANDO

                            ruta != null &&
                                    estadoRuta == EstadoRuta.EN_CURSO.name -> EstadoRecolectorVista.EN_RUTA

                            ruta != null -> EstadoRecolectorVista.ASIGNADA

                            doc.getString("estado").equals("DISPONIBLE", ignoreCase = true) ->
                                EstadoRecolectorVista.DISPONIBLE

                            else -> EstadoRecolectorVista.NO_DISPONIBLE
                        }

                    RecolectorRuta(
                        uid = doc.id,
                        nombre = doc.getString("name").orEmpty().ifBlank { "Recolector" },
                        fotoUrl = doc.getString("fotoUrl").orEmpty(),
                        localidad = doc.getString("localidad")
                            ?: doc.getString("zona")
                            ?: "",
                        estado = vista,
                        pendientes = pendientes,
                        rutaId = ruta?.id
                    )
                }
                .sortedWith(
                    compareBy<RecolectorRuta>(
                        { it.estado.prioridad },
                        { it.nombre.lowercase() }
                    )
                )

        val puntos =
            rutaPorRecolector.flatMap { (recolectorId, ruta) ->
                idsDe(ruta).mapNotNull { id ->
                    solicitudes[id]?.let { doc ->
                        PuntoRuta(
                            solicitudId = id,
                            recolectorId = recolectorId,
                            latitud = doc.getDouble("latitud") ?: 0.0,
                            longitud = doc.getDouble("longitud") ?: 0.0,
                            estado = when (doc.getString("estado")) {
                                EstadoSolicitud.RECOLECTADA.name -> EstadoParada.COMPLETADA
                                EstadoSolicitud.CANCELADA.name -> EstadoParada.CANCELADA
                                else -> EstadoParada.PENDIENTE
                            }
                        )
                    }
                }
            }.filter { it.latitud != 0.0 || it.longitud != 0.0 }

        _state.value = RutasFundacionState(
            recolectores = recolectores,
            puntos = puntos,
            disponibles = recolectores.count { it.estado == EstadoRecolectorVista.DISPONIBLE },
            isLoading = false,
            error = null
        )
    }


    private fun idsDe(ruta: DocumentSnapshot): List<String> {

        fun lista(campo: String): List<String> =
            (ruta.get(campo) as? List<*>)?.mapNotNull { it as? String }.orEmpty()

        return lista("ordenSolicitudes").ifEmpty { lista("solicitudesIds") }
    }


    override fun onCleared() {
        usuariosListener?.remove()
        rutasListener?.remove()
        solicitudesListeners.forEach { it.remove() }
        super.onCleared()
    }
}