package com.rebottle.app.model

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.YearMonth


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
// MODELO DE SOLICITUD
// ============================================================

data class Solicitud(
    val id: String = "",
    val usuarioId: String = "",
    val direccion: String = "",
    val materiales: List<String> = emptyList(),
    val pesoKg: Int = 0,
    val fechaRecoleccion: String = "",
    val estado: String = "",
    val creadaEn: Timestamp? = null
)


// ============================================================
// ESTADO GENERAL
// ============================================================

data class SolicitudState(

    // ========================================================
    // CREAR SOLICITUD
    // ========================================================

    val location: String = "",

    val selectedMaterials: Set<MaterialType> =
        emptySet(),

    val weightKg: Int = 20,

    val selectedDate: LocalDate =
        LocalDate.now(),

    val displayedMonth: YearMonth =
        YearMonth.now(),

    val locationError: String = "",

    val materialsError: String = "",

    val isLoading: Boolean = false,


    // ========================================================
    // HISTORIAL DE SOLICITUDES
    // ========================================================

    val solicitudes: List<Solicitud> =
        emptyList(),

    val isHistoryLoading: Boolean = true,

    val historyError: String = ""
)


// ============================================================
// VIEWMODEL
// ============================================================

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
                location = newLocation,
                locationError = ""
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
                selectedMaterials = updated,
                materialsError = ""
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
                weightKg = newWeight
            )
    }


    // ========================================================
    // FECHA
    // ========================================================

    fun selectDate(
        date: LocalDate
    ) {

        _state.value =
            _state.value.copy(
                selectedDate = date
            )
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
                locationError = error
            )
    }


    fun updateMaterialsError(
        error: String
    ) {

        _state.value =
            _state.value.copy(
                materialsError = error
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
                isLoading = loading
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

        if (currentState.location.isBlank()) {

            _state.value =
                currentState.copy(
                    locationError =
                        "Ingresa la dirección de recolección"
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


        if (usuarioActual == null) {

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
                isLoading = true,
                locationError = "",
                materialsError = ""
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

        val solicitud =
            hashMapOf<String, Any>(

                "usuarioId" to
                        usuarioActual.uid,

                "direccion" to
                        currentState.location.trim(),

                "materiales" to
                        materiales,

                "pesoKg" to
                        currentState.weightKg,

                "fechaRecoleccion" to
                        currentState
                            .selectedDate
                            .toString(),

                "estado" to
                        "PENDIENTE",

                "creadaEn" to
                        FieldValue.serverTimestamp()
            )


        // ====================================================
        // CREAR ID CONSECUTIVO Y GUARDAR
        // ====================================================

        val contadorRef =
            firestore
                .collection("contadores")
                .document("solicitudes")
        firestore.runTransaction { transaction ->

            val contadorSnapshot =
                transaction.get(contadorRef)

            val ultimoId =
                contadorSnapshot.getLong("ultimoId")
                    ?: 0L

            val nuevoNumero =
                ultimoId + 1

            val nuevoId =
                "RB-${
                    nuevoNumero
                        .toString()
                        .padStart(4, '0')
                }"

            val solicitudRef =
                firestore
                    .collection("solicitudes")
                    .document(nuevoId)


            transaction.set(
                solicitudRef,
                solicitud
            )


            transaction.set(
                contadorRef,
                mapOf(
                    "ultimoId" to nuevoNumero
                )
            )


            nuevoId

        }.addOnSuccessListener { nuevoId ->

            _state.value =
                _state.value.copy(
                    isLoading = false
                )

            println(
                "Solicitud creada con ID: $nuevoId"
            )

            onSuccess(nuevoId)

        }.addOnFailureListener { exception ->

            _state.value =
                _state.value.copy(
                    isLoading = false
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

        // Evita crear varios listeners
        solicitudesListener?.remove()


        val usuarioActual =
            auth.currentUser


        if (usuarioActual == null) {

            _state.value =
                _state.value.copy(
                    solicitudes = emptyList(),
                    isHistoryLoading = false,
                    historyError =
                        "No hay un usuario autenticado"
                )

            return
        }


        // Indicar carga
        _state.value =
            _state.value.copy(
                isHistoryLoading = true,
                historyError = ""
            )


        solicitudesListener =

            firestore
                .collection("solicitudes")
                .whereEqualTo(
                    "usuarioId",
                    usuarioActual.uid
                )
                .addSnapshotListener { snapshot, error ->


                    // =========================================
                    // ERROR
                    // =========================================

                    if (error != null) {

                        _state.value =
                            _state.value.copy(
                                isHistoryLoading = false,
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


                                // Materiales guardados en Firestore
                                val materiales =

                                    (
                                            document.get(
                                                "materiales"
                                            )
                                                    as? List<*>
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

                                    direccion =
                                        document.getString(
                                            "direccion"
                                        ) ?: "",

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

                                    estado =
                                        document.getString(
                                            "estado"
                                        ) ?: "",

                                    creadaEn =
                                        document.getTimestamp(
                                            "creadaEn"
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

                                it.creadaEn?.seconds
                                    ?: 0L

                            }.thenByDescending {

                                it.creadaEn?.nanoseconds
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
    // Cancelar Solicitud
    // ========================================================

    fun cancelarSolicitud(
        solicitudId: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {

        firestore
            .collection("solicitudes")
            .document(solicitudId)
            .update(
                "estado",
                "CANCELADA"
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


    fun cargarSolicitudParaEditar(
        solicitudId: String
    ) {

        val solicitud =
            _state.value.solicitudes.find {
                it.id == solicitudId
            }
                ?: return


        // ========================================================
        // CONVERTIR MATERIALES
        // ========================================================

        val materiales =
            solicitud.materiales
                .mapNotNull { material ->

                    try {

                        MaterialType.valueOf(
                            material
                        )

                    } catch (e: Exception) {

                        null
                    }
                }
                .toSet()


        // ========================================================
        // CONVERTIR FECHA
        // ========================================================

        val fecha =
            try {

                LocalDate.parse(
                    solicitud.fechaRecoleccion
                )

            } catch (e: Exception) {

                LocalDate.now()
            }


        // ========================================================
        // CARGAR DATOS EN EL FORMULARIO
        // ========================================================

        _state.value =
            _state.value.copy(

                location =
                    solicitud.direccion,

                selectedMaterials =
                    materiales,

                weightKg =
                    solicitud.pesoKg,

                selectedDate =
                    fecha,

                displayedMonth =
                    YearMonth.from(fecha),

                locationError =
                    "",

                materialsError =
                    ""
            )
    }

    fun editarSolicitud(
        solicitudId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {

        val currentState =
            _state.value


        // ========================================================
        // VALIDAR DIRECCIÓN
        // ========================================================

        if (currentState.location.isBlank()) {

            _state.value =
                currentState.copy(
                    locationError =
                        "Ingresa la dirección de recolección"
                )

            return
        }


        // ========================================================
        // VALIDAR MATERIALES
        // ========================================================

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


        // ========================================================
        // LOADING
        // ========================================================

        _state.value =
            currentState.copy(
                isLoading = true,
                locationError = "",
                materialsError = ""
            )


        // ========================================================
        // MATERIALES
        // ========================================================

        val materiales =
            currentState
                .selectedMaterials
                .map {
                    it.name
                }


        // ========================================================
        // CAMPOS A ACTUALIZAR
        // ========================================================

        val cambios =
            mapOf(

                "direccion" to
                        currentState.location.trim(),

                "materiales" to
                        materiales,

                "pesoKg" to
                        currentState.weightKg,

                "fechaRecoleccion" to
                        currentState
                            .selectedDate
                            .toString()
            )


        // ========================================================
        // ACTUALIZAR FIRESTORE
        // ========================================================

        firestore
            .collection("solicitudes")
            .document(solicitudId)
            .update(cambios)

            .addOnSuccessListener {

                _state.value =
                    _state.value.copy(
                        isLoading = false
                    )

                onSuccess()
            }

            .addOnFailureListener { exception ->

                _state.value =
                    _state.value.copy(
                        isLoading = false
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
         * Importante:
         *
         * NO hacemos:
         *
         * _state.value = SolicitudState()
         *
         * porque eso también borraría temporalmente
         * el historial de solicitudes.
         */

        _state.value =
            _state.value.copy(

                location = "",

                selectedMaterials =
                    emptySet(),

                weightKg = 20,

                selectedDate =
                    LocalDate.now(),

                displayedMonth =
                    YearMonth.now(),

                locationError = "",

                materialsError = "",

                isLoading = false
            )
    }


    // ========================================================
    // ELIMINAR LISTENER
    // ========================================================

    override fun onCleared() {

        solicitudesListener?.remove()

        super.onCleared()
    }
}