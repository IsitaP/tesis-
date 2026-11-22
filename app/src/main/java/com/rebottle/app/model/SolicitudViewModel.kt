package com.rebottle.app.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.YearMonth
import com.google.firebase.firestore.SetOptions


// ============================================================
// TIPOS DE MATERIAL
// ============================================================

enum class MaterialType(val label: String) {
    PLASTICO("Plástico"),
    PAPEL("Papel"),
    CARTON("Cartón"),
    VIDRIO("Vidrio"),
    METAL("Metal"),
    ELECTRONICOS("Electrónicos"),
    MADERA("Madera"),
    OTROS("Otros")
}


// ============================================================
// ESTADOS DE SOLICITUD
// ============================================================

enum class EstadoSolicitud {
    PUBLICADA,
    EN_RUTA,
    RECOLECTADA,
    CANCELADA
}


// ============================================================
// MODELO DE SOLICITUD
// ============================================================

data class Solicitud(

    val id: String = "",

    val usuarioId: String = "",

    val materiales: List<String> =
        emptyList(),

    val pesoKg: Int = 0,

    val fechaRecoleccion: String = "",

    val horaRuta: String = "",

    val estado: String = "",

    val creadaEn: Timestamp? = null,

    val completadaEn: Timestamp? = null,

    // ========================================================
    // UBICACIÓN GOOGLE PLACES
    // ========================================================

    val nombreLugar: String = "",

    val direccion: String = "",

    val placeId: String = "",

    val latitud: Double = 0.0,

    val longitud: Double = 0.0,

    // ========================================================
    // RUTA
    // ========================================================

    // null = todavía no pertenece a ninguna ruta.
    val rutaId: String? = null
)


// Las solicitudes canceladas no suman kg, recolecciones
// ni aportes; todas las demás sí.
fun Solicitud.cuentaComoAporte(): Boolean =
    !estado.equals(EstadoSolicitud.CANCELADA.name, ignoreCase = true)


// ============================================================
// ESTADO GENERAL
// ============================================================

data class SolicitudState(

    // ========================================================
    // CREAR / EDITAR SOLICITUD
    // ========================================================

    val location: String = "",

    val nombreLugar: String = "",

    val placeId: String = "",

    val latitud: Double = 0.0,

    val longitud: Double = 0.0,

    val selectedMaterials: Set<MaterialType> =
        emptySet(),

    val weightKg: Int = 20,

    val selectedDate: LocalDate =
        LocalDate.now(),

    val displayedMonth: YearMonth =
        YearMonth.now(),

    val locationError: String = "",

    val materialsError: String = "",

    val dateError: String = "",

    val isLoading: Boolean = false,


    // ========================================================
    // HISTORIAL
    // ========================================================

    val solicitudes: List<Solicitud> =
        emptyList(),

    val isHistoryLoading: Boolean = true,

    val historyError: String = ""
)


// ============================================================
// VIEWMODEL
// ============================================================

private const val ERROR_FECHA_PASADA =
    "Selecciona una fecha de hoy en adelante"

class SolicitudViewModel : ViewModel() {


    // ========================================================
    // FIREBASE
    // ========================================================

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()


    // ========================================================
    // STATE
    // ========================================================

    private val _state =
        MutableStateFlow(
            SolicitudState()
        )

    val state: StateFlow<SolicitudState> =
        _state.asStateFlow()


    // ========================================================
    // LISTENER DE FIRESTORE
    // ========================================================

    private var solicitudesListener:
            ListenerRegistration? = null


    // ========================================================
    // INICIO
    // ========================================================

    init {

        escucharSolicitudesUsuario()
    }


    // ========================================================
    // DIRECCIÓN
    // ========================================================

    fun updateLocation(
        newLocation: String
    ) {

        _state.value =
            _state.value.copy(

                location =
                    newLocation,

                locationError =
                    ""
            )
    }


    // ========================================================
    // UBICACIÓN SELECCIONADA DESDE GOOGLE PLACES
    // ========================================================

    fun updateLocationFromPlaces(

        nombreLugar: String,

        direccion: String,

        placeId: String,

        latitud: Double,

        longitud: Double
    ) {

        _state.value =
            _state.value.copy(

                location =
                    direccion,

                nombreLugar =
                    nombreLugar,

                placeId =
                    placeId,

                latitud =
                    latitud,

                longitud =
                    longitud,

                locationError =
                    ""
            )
    }


    // ========================================================
    // BORRAR UBICACIÓN SELECCIONADA
    // ========================================================

    fun clearLocationSelection() {

        _state.value =
            _state.value.copy(

                location =
                    "",

                nombreLugar =
                    "",

                placeId =
                    "",

                latitud =
                    0.0,

                longitud =
                    0.0,

                locationError =
                    ""
            )
    }


    // ========================================================
    // MATERIALES
    // ========================================================

    fun toggleMaterial(
        material: MaterialType
    ) {

        val current =
            _state.value.selectedMaterials


        val updated =

            if (material in current) {

                current - material

            } else {

                current + material
            }


        _state.value =
            _state.value.copy(

                selectedMaterials =
                    updated,

                materialsError =
                    ""
            )
    }


    // ========================================================
    // PESO
    // ========================================================

    fun incrementWeight() {

        _state.value =
            _state.value.copy(

                weightKg =
                    _state.value.weightKg + 1
            )
    }


    fun decrementWeight() {

        val newWeight =
            (_state.value.weightKg - 1)
                .coerceAtLeast(1)


        _state.value =
            _state.value.copy(

                weightKg =
                    newWeight
            )
    }


    // ========================================================
    // FECHA
    // ========================================================

    fun selectDate(
        date: LocalDate
    ) {

        // No se permite programar recolecciones en días anteriores a hoy
        if (date.isBefore(LocalDate.now())) {

            _state.value =
                _state.value.copy(
                    dateError = ERROR_FECHA_PASADA
                )

            return
        }

        _state.value =
            _state.value.copy(

                selectedDate =
                    date,

                dateError =
                    ""
            )
    }


    // Revisa la fecha antes de crear o editar (por si cambió el día con la pantalla abierta)
    fun validarFecha(): Boolean {

        val valida = !_state.value.selectedDate.isBefore(LocalDate.now())

        _state.value =
            _state.value.copy(
                dateError = if (valida) "" else ERROR_FECHA_PASADA
            )

        return valida
    }


    fun nextMonth() {

        _state.value =
            _state.value.copy(

                displayedMonth =
                    _state.value
                        .displayedMonth
                        .plusMonths(1)
            )
    }


    fun previousMonth() {

        _state.value =
            _state.value.copy(

                displayedMonth =
                    _state.value
                        .displayedMonth
                        .minusMonths(1)
            )
    }


    // ========================================================
    // ERRORES DEL FORMULARIO
    // ========================================================

    fun updateLocationError(
        error: String
    ) {

        _state.value =
            _state.value.copy(

                locationError =
                    error
            )
    }


    fun updateMaterialsError(
        error: String
    ) {

        _state.value =
            _state.value.copy(

                materialsError =
                    error
            )
    }


    // ========================================================
    // LOADING
    // ========================================================

    fun updateLoading(
        loading: Boolean
    ) {

        _state.value =
            _state.value.copy(

                isLoading =
                    loading
            )
    }


    // ========================================================
    // CREAR SOLICITUD
    // ========================================================

    fun crearSolicitud(

        onSuccess: (String) -> Unit,

        onError: (String) -> Unit
    ) {

        val currentState =
            _state.value


        // ====================================================
        // VALIDAR DIRECCIÓN
        // ====================================================

        if (
            currentState.location.isBlank()
        ) {

            _state.value =
                currentState.copy(

                    locationError =
                        "Ingresa la dirección de recolección"
                )

            return
        }


        // ====================================================
        // VALIDAR UBICACIÓN GOOGLE PLACES
        // ====================================================

        if (
            currentState.placeId.isBlank()
        ) {

            _state.value =
                currentState.copy(

                    locationError =
                        "Selecciona una dirección válida de la lista"
                )

            return
        }


        // ====================================================
        // VALIDAR MATERIALES
        // ====================================================

        if (
            currentState
                .selectedMaterials
                .isEmpty()
        ) {

            _state.value =
                currentState.copy(

                    materialsError =
                        "Selecciona al menos un material"
                )

            return
        }


        // ====================================================
        // USUARIO ACTUAL
        // ====================================================

        val usuarioActual =
            auth.currentUser


        if (
            usuarioActual == null
        ) {

            onError(
                "No hay un usuario autenticado"
            )

            return
        }


        // ====================================================
        // ACTIVAR LOADING
        // ====================================================

        _state.value =
            currentState.copy(

                isLoading =
                    true,

                locationError =
                    "",

                materialsError =
                    ""
            )


        // ====================================================
        // CONVERTIR MATERIALES
        // ====================================================

        val materiales =

            currentState
                .selectedMaterials
                .map {

                    it.name
                }


        // ====================================================
        // CREAR MAP DE FIRESTORE
        // ====================================================

        /*
         * IMPORTANTE:
         *
         * usamos Any? en vez de Any
         * porque rutaId comienza en null.
         */

        val solicitud =
            hashMapOf<String, Any?>(

                "usuarioId" to
                        usuarioActual.uid,


                // =============================================
                // GOOGLE PLACES
                // =============================================

                "nombreLugar" to
                        currentState
                            .nombreLugar
                            .trim(),

                "direccion" to
                        currentState
                            .location
                            .trim(),

                "placeId" to
                        currentState.placeId,

                "latitud" to
                        currentState.latitud,

                "longitud" to
                        currentState.longitud,


                // =============================================
                // SOLICITUD
                // =============================================

                "materiales" to
                        materiales,

                "pesoKg" to
                        currentState.weightKg,

                "fechaRecoleccion" to
                        currentState
                            .selectedDate
                            .toString(),


                // Toda solicitud nueva se publica
                // automáticamente.
                "estado" to
                        EstadoSolicitud
                            .PUBLICADA
                            .name,


                // Todavía no pertenece a ninguna ruta.
                "rutaId" to
                        null,


                "creadaEn" to
                        FieldValue
                            .serverTimestamp()
            )


        // ====================================================
        // CREAR ID CONSECUTIVO Y GUARDAR
        // ====================================================

        val contadorRef =

            firestore
                .collection(
                    "contadores"
                )
                .document(
                    "solicitudes"
                )


        firestore
            .runTransaction { transaction ->


                val contadorSnapshot =
                    transaction.get(
                        contadorRef
                    )


                val ultimoId =
                    contadorSnapshot
                        .getLong(
                            "ultimoId"
                        )
                        ?: 0L


                val nuevoNumero =
                    ultimoId + 1


                val nuevoId =
                    "RB-${
                        nuevoNumero
                            .toString()
                            .padStart(
                                4,
                                '0'
                            )
                    }"


                val solicitudRef =

                    firestore
                        .collection(
                            "solicitudes"
                        )
                        .document(
                            nuevoId
                        )


                transaction.set(

                    solicitudRef,

                    solicitud
                )


                transaction.set(

                    contadorRef,

                    mapOf(
                        "ultimoId" to
                                nuevoNumero
                    )
                )


                nuevoId

            }
            .addOnSuccessListener { nuevoId ->


                _state.value =
                    _state.value.copy(

                        isLoading =
                            false
                    )


                println(
                    "Solicitud creada con ID: $nuevoId"
                )


                onSuccess(
                    nuevoId
                )
            }
            .addOnFailureListener { exception ->


                _state.value =
                    _state.value.copy(

                        isLoading =
                            false
                    )


                onError(

                    exception.message
                        ?: "Error al crear la solicitud"
                )
            }
    }


    // ========================================================
    // ESCUCHAR HISTORIAL DEL USUARIO EN TIEMPO REAL
    // ========================================================

    fun escucharSolicitudesUsuario() {

        // Evita crear varios listeners.
        solicitudesListener
            ?.remove()


        val usuarioActual =
            auth.currentUser


        if (
            usuarioActual == null
        ) {

            _state.value =
                _state.value.copy(

                    solicitudes =
                        emptyList(),

                    isHistoryLoading =
                        false,

                    historyError =
                        "No hay un usuario autenticado"
                )

            return
        }


        _state.value =
            _state.value.copy(

                isHistoryLoading =
                    true,

                historyError =
                    ""
            )


        escucharConsulta(
            firestore
                .collection("solicitudes")
                .whereEqualTo("usuarioId", usuarioActual.uid)
        )
    }


    // ========================================================
    // ESCUCHAR APORTES QUE RECIBE LA FUNDACIÓN
    // ========================================================

    // La fundación recibe todo lo que ya se recolectó, venga del
    // usuario que venga. Llena el mismo state.solicitudes, así las
    // pantallas de aportes funcionan igual que en el usuario.
    fun escucharSolicitudesFundacion() {

        if (auth.currentUser == null) {

            solicitudesListener?.remove()

            _state.value =
                _state.value.copy(
                    solicitudes = emptyList(),
                    isHistoryLoading = false,
                    historyError = "No hay un usuario autenticado"
                )

            return
        }

        _state.value =
            _state.value.copy(
                isHistoryLoading = true,
                historyError = ""
            )

        escucharConsulta(
            firestore
                .collection("solicitudes")
                .whereEqualTo("estado", EstadoSolicitud.RECOLECTADA.name)
        )
    }


    // ========================================================
    // LISTENER COMPARTIDO (usuario y fundación)
    // ========================================================

    private fun escucharConsulta(
        consulta: Query
    ) {

        // Evita crear varios listeners.
        solicitudesListener
            ?.remove()


        solicitudesListener =

            consulta
                .addSnapshotListener { snapshot, error ->


                    // =========================================
                    // ERROR
                    // =========================================

                    if (
                        error != null
                    ) {

                        _state.value =
                            _state.value.copy(

                                isHistoryLoading =
                                    false,

                                historyError =
                                    error.message
                                        ?: "Error al cargar solicitudes"
                            )

                        return@addSnapshotListener
                    }


                    // =========================================
                    // CONVERTIR DOCUMENTOS
                    // =========================================

                    val solicitudes =

                        snapshot
                            ?.documents
                            ?.map { document ->


                                val materiales =

                                    (
                                            document.get(
                                                "materiales"
                                            ) as? List<*>
                                            )
                                        ?.mapNotNull {

                                            it as? String
                                        }
                                        ?: emptyList()


                                Solicitud(

                                    id =
                                        document.id,


                                    usuarioId =
                                        document.getString(
                                            "usuarioId"
                                        ) ?: "",


                                    nombreLugar =
                                        document.getString(
                                            "nombreLugar"
                                        ) ?: "",


                                    direccion =
                                        document.getString(
                                            "direccion"
                                        ) ?: "",


                                    placeId =
                                        document.getString(
                                            "placeId"
                                        ) ?: "",


                                    latitud =
                                        document.getDouble(
                                            "latitud"
                                        ) ?: 0.0,


                                    longitud =
                                        document.getDouble(
                                            "longitud"
                                        ) ?: 0.0,


                                    materiales =
                                        materiales,


                                    pesoKg =
                                        document.getLong(
                                            "pesoKg"
                                        )
                                            ?.toInt()
                                            ?: 0,


                                    fechaRecoleccion =
                                        document.getString(
                                            "fechaRecoleccion"
                                        ) ?: "",


                                    horaRuta =
                                        document.getString(
                                            "horaRuta"
                                        ) ?: "",

                                    estado =
                                        document.getString(
                                            "estado"
                                        ) ?: "",


                                    // =================================
                                    // RUTA
                                    // =================================

                                    rutaId =
                                        document.getString(
                                            "rutaId"
                                        ),


                                    creadaEn =
                                        document.getTimestamp(
                                            "creadaEn"
                                        ),
                                    completadaEn =
                                        document.getTimestamp(
                                            "completadaEn"
                                        )
                                )
                            }
                            ?: emptyList()


                    // =========================================
                    // ORDENAR MÁS RECIENTES PRIMERO
                    // =========================================

                    val solicitudesOrdenadas =

                        solicitudes.sortedWith(

                            compareByDescending<Solicitud> {

                                it.creadaEn
                                    ?.seconds
                                    ?: 0L

                            }.thenByDescending {

                                it.creadaEn
                                    ?.nanoseconds
                                    ?: 0
                            }
                        )


                    // =========================================
                    // ACTUALIZAR STATE
                    // =========================================

                    _state.value =
                        _state.value.copy(

                            solicitudes =
                                solicitudesOrdenadas,

                            isHistoryLoading =
                                false,

                            historyError =
                                ""
                        )
                }
    }


    // ========================================================
    // CANCELAR SOLICITUD
    // ========================================================

    fun cancelarSolicitud(

        solicitudId: String,

        onSuccess: () -> Unit =
            {},

        onError: (String) -> Unit =
            {}
    ) {

        firestore
            .collection(
                "solicitudes"
            )
            .document(
                solicitudId
            )
            .update(

                "estado",

                EstadoSolicitud
                    .CANCELADA
                    .name
            )
            .addOnSuccessListener {

                onSuccess()
            }
            .addOnFailureListener { exception ->


                onError(

                    exception.message
                        ?: "Error al cancelar la solicitud"
                )
            }
    }
    // ========================================================
    // MARCAR SOLICITUD COMO RECOLECTADA
    // ========================================================

    fun marcarRecolectada(
        solicitudId: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {

        val solicitudRef =
            firestore
                .collection("solicitudes")
                .document(solicitudId)

        firestore
            .runTransaction { transaction ->

                // ====================================================
                // LEER LA SOLICITUD
                // ====================================================

                val solicitudSnapshot =
                    transaction.get(solicitudRef)

                if (!solicitudSnapshot.exists()) {
                    throw Exception("La solicitud no existe")
                }


                // ====================================================
                // OBTENER USUARIO Y PESO
                // ====================================================

                val usuarioId =
                    solicitudSnapshot
                        .getString("usuarioId")
                        ?: throw Exception(
                            "La solicitud no tiene usuario asociado"
                        )

                val pesoKg =
                    solicitudSnapshot
                        .getLong("pesoKg")
                        ?: 0L


                // ====================================================
                // EVITAR DAR LOS PUNTOS DOS VECES
                // ====================================================

                val puntosYaOtorgados =
                    solicitudSnapshot
                        .getBoolean("puntosOtorgados")
                        ?: false

                if (puntosYaOtorgados) {

                    // Ya se habían entregado anteriormente
                    return@runTransaction 0L
                }


                // ====================================================
                // CALCULAR PUNTOS
                // 1 KG = 5 PUNTOS
                // ====================================================

                val puntosGanados =
                    pesoKg * 5L


                // ====================================================
                // DOCUMENTO DE PUNTOS DEL USUARIO
                // ====================================================

                val progresoRef =
                    firestore
                        .collection("progresoAprendizaje")
                        .document(usuarioId)


                // ====================================================
                // ACTUALIZAR SOLICITUD
                // ====================================================

                transaction.update(
                    solicitudRef,
                    mapOf(
                        "estado" to
                                EstadoSolicitud.RECOLECTADA.name,

                        "completadaEn" to
                                FieldValue.serverTimestamp(),

                        // Sirve para evitar volver a entregar puntos
                        "puntosOtorgados" to
                                true,

                        // Solo para poder saber cuántos ganó
                        "puntosGanados" to
                                puntosGanados
                    )
                )


                // ====================================================
                // SUMAR PUNTOS AL USUARIO
                // ====================================================

                transaction.set(
                    progresoRef,
                    mapOf(
                        "usuarioId" to
                                usuarioId,

                        "puntosTotales" to
                                FieldValue.increment(
                                    puntosGanados
                                ),

                        "actualizadoEn" to
                                FieldValue.serverTimestamp()
                    ),
                    SetOptions.merge()
                )


                puntosGanados
            }

            .addOnSuccessListener { puntosGanados ->

                Log.d(
                    "PUNTOS_SOLICITUD",
                    "Solicitud $solicitudId recolectada. " +
                            "Puntos ganados: $puntosGanados"
                )

                onSuccess()
            }

            .addOnFailureListener { exception ->

                Log.e(
                    "PUNTOS_SOLICITUD",
                    "Error completando solicitud",
                    exception
                )

                onError(
                    exception.message
                        ?: "Error al completar la solicitud"
                )
            }
    }


    // ========================================================
    // CARGAR SOLICITUD PARA EDITAR
    // ========================================================

    fun cargarSolicitudParaEditar(
        solicitudId: String
    ) {

        val solicitud =

            _state.value
                .solicitudes
                .find {

                    it.id ==
                            solicitudId
                }
                ?: return


        // ====================================================
        // CONVERTIR MATERIALES
        // ====================================================

        val materiales =

            solicitud
                .materiales
                .mapNotNull { material ->


                    try {

                        MaterialType
                            .valueOf(
                                material
                            )

                    } catch (
                        e: Exception
                    ) {

                        null
                    }
                }
                .toSet()


        // ====================================================
        // CONVERTIR FECHA
        // ====================================================

        val fecha =

            try {

                LocalDate.parse(
                    solicitud.fechaRecoleccion
                )

            } catch (
                e: Exception
            ) {

                LocalDate.now()
            }


        // ====================================================
        // CARGAR DATOS EN EL FORMULARIO
        // ====================================================

        _state.value =
            _state.value.copy(

                location =
                    solicitud.direccion,

                nombreLugar =
                    solicitud.nombreLugar,

                placeId =
                    solicitud.placeId,

                latitud =
                    solicitud.latitud,

                longitud =
                    solicitud.longitud,

                selectedMaterials =
                    materiales,

                weightKg =
                    solicitud.pesoKg,

                selectedDate =
                    fecha,

                displayedMonth =
                    YearMonth.from(
                        fecha
                    ),

                locationError =
                    "",

                materialsError =
                    ""
            )
    }


    // ========================================================
    // EDITAR SOLICITUD
    // ========================================================

    fun editarSolicitud(

        solicitudId: String,

        onSuccess: () -> Unit,

        onError: (String) -> Unit
    ) {

        val currentState =
            _state.value


        // ====================================================
        // VALIDAR DIRECCIÓN
        // ====================================================

        if (
            currentState.location.isBlank()
        ) {

            _state.value =
                currentState.copy(

                    locationError =
                        "Ingresa la dirección de recolección"
                )

            return
        }


        // ====================================================
        // VALIDAR UBICACIÓN GOOGLE PLACES
        // ====================================================

        if (
            currentState.placeId.isBlank()
        ) {

            _state.value =
                currentState.copy(

                    locationError =
                        "Selecciona una dirección válida de la lista"
                )

            return
        }


        // ====================================================
        // VALIDAR MATERIALES
        // ====================================================

        if (
            currentState
                .selectedMaterials
                .isEmpty()
        ) {

            _state.value =
                currentState.copy(

                    materialsError =
                        "Selecciona al menos un material"
                )

            return
        }


        // ====================================================
        // LOADING
        // ====================================================

        _state.value =
            currentState.copy(

                isLoading =
                    true,

                locationError =
                    "",

                materialsError =
                    ""
            )


        // ====================================================
        // MATERIALES
        // ====================================================

        val materiales =

            currentState
                .selectedMaterials
                .map {

                    it.name
                }


        // ====================================================
        // CAMPOS A ACTUALIZAR
        // ====================================================

        val cambios =
            mapOf(

                "nombreLugar" to
                        currentState
                            .nombreLugar
                            .trim(),

                "direccion" to
                        currentState
                            .location
                            .trim(),

                "placeId" to
                        currentState.placeId,

                "latitud" to
                        currentState.latitud,

                "longitud" to
                        currentState.longitud,

                "materiales" to
                        materiales,

                "pesoKg" to
                        currentState.weightKg,

                "fechaRecoleccion" to
                        currentState
                            .selectedDate
                            .toString()
            )


        // ====================================================
        // ACTUALIZAR FIRESTORE
        // ====================================================

        firestore
            .collection(
                "solicitudes"
            )
            .document(
                solicitudId
            )
            .update(
                cambios
            )
            .addOnSuccessListener {


                _state.value =
                    _state.value.copy(

                        isLoading =
                            false
                    )


                onSuccess()
            }
            .addOnFailureListener { exception ->


                _state.value =
                    _state.value.copy(

                        isLoading =
                            false
                    )


                onError(

                    exception.message
                        ?: "Error al editar la solicitud"
                )
            }
    }


    // ========================================================
    // REINICIAR FORMULARIO
    // ========================================================

    fun resetForm() {

        /*
         * No hacemos:
         *
         * _state.value = SolicitudState()
         *
         * porque eso también borraría temporalmente
         * el historial de solicitudes.
         */

        _state.value =
            _state.value.copy(

                location =
                    "",

                nombreLugar =
                    "",

                placeId =
                    "",

                latitud =
                    0.0,

                longitud =
                    0.0,

                selectedMaterials =
                    emptySet(),

                weightKg =
                    20,

                selectedDate =
                    LocalDate.now(),

                displayedMonth =
                    YearMonth.now(),

                locationError =
                    "",

                materialsError =
                    "",

                dateError =
                    "",

                isLoading =
                    false
            )
    }


    // ========================================================
    // ELIMINAR LISTENER
    // ========================================================

    override fun onCleared() {

        solicitudesListener
            ?.remove()

        super.onCleared()
    }
}