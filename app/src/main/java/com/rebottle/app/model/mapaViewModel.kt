package com.rebottle.app.model

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


// ============================================================
// ESTADOS DE UNA RUTA
// ============================================================

enum class EstadoRuta {
    PUBLICADA,
    ASIGNADA,
    EN_CURSO,
    COMPLETADA,
    CANCELADA
}


// ============================================================
// MODELO DE RUTA
// ============================================================

data class Ruta(

    val id: String = "",

    val solicitudesIds: List<String> = emptyList(),

    val fechaRecoleccion: String = "",

    // Hora de salida que define la fundación ("HH:mm").
    // Vacía = todavía no se ha definido.
    val hora: String = "",

    val estado: String = EstadoRuta.PUBLICADA.name,

    val recolectorId: String? = null,

    val ordenSolicitudes: List<String> = emptyList(),

    val distanciaTotalMetros: Long = 0L,

    val duracionTotalSegundos: Long = 0L,

    val polyline: String = "",

    val creadaEn: Timestamp? = null
)


// ============================================================
// ESTADO DE LA UI DE RUTAS
// ============================================================

data class RutasState(

    val rutasPublicadas: List<Ruta> = emptyList(),

    val isLoading: Boolean = false,

    val error: String? = null
)


data class AsignacionRutaState(

    val isLoading: Boolean = false,

    val recolectorId: String? = null,

    val asignacionExitosa: Boolean = false,

    val error: String? = null
)


data class DesasignacionRutaState(
    val isLoading: Boolean = false,
    val desasignacionExitosa: Boolean = false,
    val error: String? = null
)


// ============================================================
// MAPA VIEW MODEL
// ============================================================

class MapaViewModel : ViewModel() {


    // ========================================================
    // FIRESTORE
    // ========================================================

    private val firestore =
        FirebaseFirestore.getInstance()


    // ========================================================
    // ESTADO DE RUTAS
    // ========================================================

    private val _rutasState =
        MutableStateFlow(
            RutasState()
        )


    val rutasState: StateFlow<RutasState> =
        _rutasState.asStateFlow()


    // ========================================================
    // ESTADO DE ASGINACION
    // ========================================================



    private val _asignacionState =
        MutableStateFlow(
            AsignacionRutaState()
        )

    val asignacionState: StateFlow<AsignacionRutaState> =
        _asignacionState.asStateFlow()


    // ========================================================
    // ESTADO DE DESASIGNACION
    // ========================================================


    private val _desasignacionState =
        MutableStateFlow(
            DesasignacionRutaState()
        )

    val desasignacionState: StateFlow<DesasignacionRutaState> =
        _desasignacionState.asStateFlow()


    // ========================================================
    // LISTENER DE FIRESTORE
    // ========================================================

    private var rutasListener:
            ListenerRegistration? = null


    // ========================================================
    // ESCUCHAR RUTAS PUBLICADAS
    // ========================================================

    fun escucharRutasPublicadas() {


        // Evita tener varios listeners activos
        // al mismo tiempo.
        rutasListener?.remove()


        _rutasState.value =
            _rutasState.value.copy(
                isLoading = true,
                error = null
            )


        rutasListener =
            firestore
                .collection("rutas")

                .whereIn(
                    "estado",
                    listOf(
                        EstadoRuta.PUBLICADA.name,
                        EstadoRuta.ASIGNADA.name
                    )
                )

                .addSnapshotListener { snapshot, exception ->


                    // =================================================
                    // ERROR DE FIRESTORE
                    // =================================================

                    if (exception != null) {

                        _rutasState.value =
                            _rutasState.value.copy(
                                isLoading = false,

                                error =
                                    exception.message
                                        ?: "Error al cargar las rutas"
                            )

                        return@addSnapshotListener
                    }


                    // =================================================
                    // SNAPSHOT NULO
                    // =================================================

                    if (snapshot == null) {

                        _rutasState.value =
                            _rutasState.value.copy(
                                isLoading = false,

                                error =
                                    "No se pudieron cargar las rutas"
                            )

                        return@addSnapshotListener
                    }


                    // =================================================
                    // CONVERTIR DOCUMENTOS DE FIRESTORE A Ruta
                    // =================================================

                    val rutas =
                        snapshot.documents
                            .map { document ->


                                // -------------------------------------
                                // solicitudesIds
                                // -------------------------------------

                                val solicitudesIds =
                                    (
                                            document.get(
                                                "solicitudesIds"
                                            ) as? List<*>
                                            )
                                        ?.mapNotNull {
                                            it as? String
                                        }
                                        ?: emptyList()


                                // -------------------------------------
                                // ordenSolicitudes
                                // -------------------------------------

                                val ordenSolicitudes =
                                    (
                                            document.get(
                                                "ordenSolicitudes"
                                            ) as? List<*>
                                            )
                                        ?.mapNotNull {
                                            it as? String
                                        }
                                        ?: emptyList()


                                // -------------------------------------
                                // Crear objeto Ruta
                                // -------------------------------------

                                Ruta(

                                    id =
                                        document.id,


                                    solicitudesIds =
                                        solicitudesIds,


                                    fechaRecoleccion =
                                        document.getString(
                                            "fechaRecoleccion"
                                        ) ?: "",
                                    hora =
                                        document.getString(
                                            "hora"
                                        ) ?: "",


                                    estado =
                                        document.getString(
                                            "estado"
                                        )
                                            ?: EstadoRuta.PUBLICADA.name,


                                    recolectorId =
                                        document.getString(
                                            "recolectorId"
                                        ),


                                    ordenSolicitudes =
                                        ordenSolicitudes,


                                    distanciaTotalMetros =
                                        document.getLong(
                                            "distanciaTotalMetros"
                                        ) ?: 0L,


                                    duracionTotalSegundos =
                                        document.getLong(
                                            "duracionTotalSegundos"
                                        ) ?: 0L,


                                    polyline =
                                        document.getString(
                                            "polyline"
                                        ) ?: "",


                                    creadaEn =
                                        document.getTimestamp(
                                            "creadaEn"
                                        )
                                )
                            }


                            // =========================================
                            // RUTAS MÁS RECIENTES PRIMERO
                            // =========================================

                            .sortedByDescending {
                                it.creadaEn
                            }


                    // =================================================
                    // ACTUALIZAR ESTADO
                    // =================================================

                    _rutasState.value =
                        RutasState(

                            rutasPublicadas =
                                rutas,

                            isLoading =
                                false,

                            error =
                                null
                        )
                }
    }


    // ========================================================
    // DETENER LISTENER MANUALMENTE
    // ========================================================

    fun detenerEscuchaRutas() {

        rutasListener?.remove()

        rutasListener = null
    }


    // ========================================================
    // LIMPIAR VIEW MODEL
    // ========================================================

    override fun onCleared() {

        rutasListener?.remove()

        rutasListener = null

        super.onCleared()
    }

    fun asignarRecolectorARuta(
        rutaId: String,
        recolectorId: String
    ) {

        // ========================================================
        // VALIDACIÓN BÁSICA
        // ========================================================

        if (
            rutaId.isBlank() ||
            recolectorId.isBlank()
        ) {

            _asignacionState.value =
                AsignacionRutaState(
                    error = "Los datos de la asignación no son válidos"
                )

            return
        }


        // ========================================================
        // LOADING
        // ========================================================

        _asignacionState.value =
            AsignacionRutaState(
                isLoading = true,
                recolectorId = recolectorId
            )


        // ========================================================
        // REFERENCIAS
        // ========================================================

        val rutaRef =
            firestore
                .collection("rutas")
                .document(rutaId)


        val recolectorRef =
            firestore
                .collection("users")
                .document(recolectorId)


        // ========================================================
        // TRANSACCIÓN
        // ========================================================

        firestore
            .runTransaction { transaction ->


                // ------------------------------------------------
                // LEER RUTA
                // ------------------------------------------------

                val rutaSnapshot =
                    transaction.get(
                        rutaRef
                    )


                if (!rutaSnapshot.exists()) {

                    throw IllegalStateException(
                        "La ruta ya no existe"
                    )
                }


                // ------------------------------------------------
                // LEER RECOLECTOR
                // ------------------------------------------------

                val recolectorSnapshot =
                    transaction.get(
                        recolectorRef
                    )


                if (!recolectorSnapshot.exists()) {

                    throw IllegalStateException(
                        "El recolector ya no existe"
                    )
                }


                // ------------------------------------------------
                // ESTADO ACTUAL DE LA RUTA
                // ------------------------------------------------

                val estadoRuta =
                    rutaSnapshot
                        .getString("estado")
                        ?: ""


                val recolectorActual =
                    rutaSnapshot
                        .getString("recolectorId")


                // ------------------------------------------------
                // LA RUTA DEBE SEGUIR PUBLICADA
                // ------------------------------------------------

                if (
                    estadoRuta !=
                    EstadoRuta.PUBLICADA.name
                ) {

                    throw IllegalStateException(
                        "La ruta ya no está disponible para asignación"
                    )
                }


                // ------------------------------------------------
                // LA RUTA NO DEBE TENER RECOLECTOR
                // ------------------------------------------------

                if (
                    !recolectorActual.isNullOrBlank()
                ) {

                    throw IllegalStateException(
                        "La ruta ya tiene un recolector asignado"
                    )
                }


                // ------------------------------------------------
                // VALIDAR QUE SEA RECOLECTOR
                // ------------------------------------------------

                val rol =
                    recolectorSnapshot
                        .getString("role")
                        ?: ""


                if (
                    !rol.equals(
                        "recolector",
                        ignoreCase = true
                    )
                ) {

                    throw IllegalStateException(
                        "El usuario seleccionado no es un recolector"
                    )
                }


                // ------------------------------------------------
                // VALIDAR DISPONIBILIDAD
                // ------------------------------------------------

                val estadoRecolector =
                    recolectorSnapshot
                        .getString("estado")
                        ?: ""


                if (
                    !estadoRecolector.equals(
                        "DISPONIBLE",
                        ignoreCase = true
                    )
                ) {

                    throw IllegalStateException(
                        "El recolector ya no está disponible"
                    )
                }


                // =================================================
                // SOLICITUDES INCLUIDAS EN LA RUTA
                // =================================================
                // Se leen desde la ruta, ya que el recolector se asigna
                // a toda la ruta y no individualmente a cada solicitud.

                val solicitudesIds =
                    (rutaSnapshot.get("solicitudesIds") as? List<*>)
                        ?.mapNotNull { it as? String }
                        ?.filter { it.isNotBlank() }
                        ?.distinct()
                        ?: emptyList()

                if (solicitudesIds.isEmpty()) {
                    throw IllegalStateException(
                        "La ruta no contiene solicitudes para asignar"
                    )
                }


                // =================================================
                // ACTUALIZAR RUTA
                // =================================================

                transaction.update(

                    rutaRef,

                    mapOf(

                        "estado" to
                                EstadoRuta.ASIGNADA.name,

                        "recolectorId" to
                                recolectorId,

                        "asignadaEn" to
                                FieldValue.serverTimestamp()
                    )
                )


                // =================================================
                // ASIGNAR EL RECOLECTOR A TODAS LAS SOLICITUDES
                // =================================================
                // Esto permite que el ciudadano lea el recolectorId
                // desde su propia solicitud, sin acceder a las rutas.
                // Se ejecuta dentro de la misma transacción para evitar
                // que la ruta y sus solicitudes queden desincronizadas.

                solicitudesIds.forEach { solicitudId ->
                    val solicitudRef = firestore
                        .collection("solicitudes")
                        .document(solicitudId)

                    transaction.update(
                        solicitudRef,
                        "recolectorId",
                        recolectorId
                    )
                }


                // =================================================
                // ACTUALIZAR RECOLECTOR
                // =================================================

                transaction.update(

                    recolectorRef,

                    mapOf(

                        "estado" to
                                "OCUPADO"
                    )
                )


                true
            }


            // ====================================================
            // ÉXITO
            // ====================================================

            .addOnSuccessListener {

                _asignacionState.value =
                    AsignacionRutaState(

                        isLoading = false,

                        recolectorId =
                            recolectorId,

                        asignacionExitosa =
                            true
                    )
            }


            // ====================================================
            // ERROR
            // ====================================================

            .addOnFailureListener {
                    exception ->


                _asignacionState.value =
                    AsignacionRutaState(

                        isLoading = false,

                        error =
                            exception.message
                                ?: "No se pudo asignar el recolector"
                    )
            }
    }

    // ========================================================
    // HORA DE LA RUTA
    // ========================================================

    // Se guarda como "HH:mm" (24 h). El Inicio de la fundación
    // la usa para saber cuál es la próxima ruta.
    fun actualizarHoraRuta(
        rutaId: String,
        hora: String,
        onError: (String) -> Unit = {}
    ) {

        if (rutaId.isBlank()) {
            return
        }


        val rutaRef =
            firestore
                .collection("rutas")
                .document(rutaId)


        firestore
            .runTransaction { transaction ->

                // ====================================================
                // OBTENER LA RUTA
                // ====================================================

                val rutaSnapshot =
                    transaction.get(rutaRef)


                if (!rutaSnapshot.exists()) {
                    throw IllegalStateException(
                        "La ruta no existe"
                    )
                }


                // ====================================================
                // OBTENER LAS SOLICITUDES DE LA RUTA
                // ====================================================

                val solicitudesIds =
                    (
                            rutaSnapshot.get(
                                "solicitudesIds"
                            ) as? List<*>
                            )
                        ?.mapNotNull {
                            it as? String
                        }
                        ?: emptyList()


                // ====================================================
                // ACTUALIZAR HORA DE LA RUTA
                // ====================================================

                transaction.update(
                    rutaRef,
                    "hora",
                    hora
                )


                // ====================================================
                // ACTUALIZAR HORA DE CADA SOLICITUD
                // ====================================================

                solicitudesIds.forEach { solicitudId ->

                    val solicitudRef =
                        firestore
                            .collection("solicitudes")
                            .document(solicitudId)


                    transaction.update(
                        solicitudRef,
                        "horaRuta",
                        hora
                    )
                }
            }

            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "No se pudo guardar la hora"
                )
            }
    }


    fun limpiarEstadoAsignacion() {

        _asignacionState.value =
            AsignacionRutaState()
    }


    fun desasignarRecolectorDeRuta(
        rutaId: String
    ) {

        if (rutaId.isBlank()) {

            _desasignacionState.value =
                DesasignacionRutaState(
                    error = "La ruta no es válida"
                )

            return
        }


        _desasignacionState.value =
            DesasignacionRutaState(
                isLoading = true
            )


        val rutaRef =
            firestore
                .collection("rutas")
                .document(rutaId)


        firestore
            .runTransaction { transaction ->


                // ================================================
                // OBTENER RUTA
                // ================================================

                val rutaSnapshot =
                    transaction.get(
                        rutaRef
                    )


                if (!rutaSnapshot.exists()) {

                    throw IllegalStateException(
                        "La ruta no existe"
                    )
                }


                // ================================================
                // VALIDAR ESTADO
                // ================================================

                val estadoRuta =
                    rutaSnapshot
                        .getString("estado")
                        ?: ""


                if (
                    estadoRuta !=
                    EstadoRuta.ASIGNADA.name
                ) {

                    throw IllegalStateException(
                        "Solo se puede desasignar un recolector de una ruta asignada"
                    )
                }


                // ================================================
                // OBTENER RECOLECTOR
                // ================================================

                val recolectorId =
                    rutaSnapshot
                        .getString("recolectorId")


                if (
                    recolectorId.isNullOrBlank()
                ) {

                    throw IllegalStateException(
                        "La ruta no tiene un recolector asignado"
                    )
                }


                val recolectorRef =
                    firestore
                        .collection("users")
                        .document(recolectorId)


                val recolectorSnapshot =
                    transaction.get(
                        recolectorRef
                    )


                if (!recolectorSnapshot.exists()) {

                    throw IllegalStateException(
                        "El recolector asignado no existe"
                    )
                }


                // ================================================
                // SOLICITUDES INCLUIDAS EN LA RUTA
                // ================================================

                val solicitudesIds =
                    (rutaSnapshot.get("solicitudesIds") as? List<*>)
                        ?.mapNotNull { it as? String }
                        ?.filter { it.isNotBlank() }
                        ?.distinct()
                        ?: emptyList()

                if (solicitudesIds.isEmpty()) {
                    throw IllegalStateException(
                        "La ruta no contiene solicitudes para desasignar"
                    )
                }


                // ================================================
                // RUTA VUELVE A PUBLICADA
                // ================================================

                transaction.update(
                    rutaRef,
                    mapOf(
                        "estado" to
                                EstadoRuta.PUBLICADA.name,

                        "recolectorId" to
                                null,

                        "asignadaEn" to
                                FieldValue.delete()
                    )
                )


                // ================================================
                // QUITAR RECOLECTOR DE TODAS LAS SOLICITUDES
                // ================================================
                // Al desasignar la ruta, el detalle del ciudadano
                // volverá a mostrar "No asignado".

                solicitudesIds.forEach { solicitudId ->
                    val solicitudRef = firestore
                        .collection("solicitudes")
                        .document(solicitudId)

                    transaction.update(
                        solicitudRef,
                        "recolectorId",
                        FieldValue.delete()
                    )
                }


                // ================================================
                // RECOLECTOR VUELVE A DISPONIBLE
                // ================================================

                transaction.update(
                    recolectorRef,
                    "estado",
                    "DISPONIBLE"
                )


                true
            }

            .addOnSuccessListener {

                _desasignacionState.value =
                    DesasignacionRutaState(
                        desasignacionExitosa = true
                    )
            }

            .addOnFailureListener { exception ->

                _desasignacionState.value =
                    DesasignacionRutaState(
                        error =
                            exception.message
                                ?: "No se pudo desasignar el recolector"
                    )
            }
    }

    fun limpiarEstadoDesasignacion() {

        _desasignacionState.value =
            DesasignacionRutaState()
    }

}
