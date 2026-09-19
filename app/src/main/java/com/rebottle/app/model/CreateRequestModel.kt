package com.rebottle.app.model

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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
// ESTADO DE LA PANTALLA
// ============================================================

data class CreateRequestState(

    val location: String = "",

    val selectedMaterials: Set<MaterialType> = emptySet(),

    val weightKg: Int = 20,

    val selectedDate: LocalDate = LocalDate.now(),

    val displayedMonth: YearMonth = YearMonth.now(),

    val locationError: String = "",

    val materialsError: String = "",

    val isLoading: Boolean = false
)


// ============================================================
// VIEWMODEL
// ============================================================

class CreateRequestViewModel : ViewModel() {


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
            CreateRequestState()
        )

    val state: StateFlow<CreateRequestState> =
        _state.asStateFlow()


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
    // ERRORES
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
    // CREAR SOLICITUD EN FIRESTORE
    // ========================================================

    fun crearSolicitud(
        onSuccess: () -> Unit,
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
            currentState.selectedMaterials.isEmpty()
        ) {

            _state.value =
                currentState.copy(
                    materialsError =
                        "Selecciona al menos un material"
                )

            return
        }


        // ====================================================
        // VALIDAR USUARIO
        // ====================================================

        val currentUser =
            auth.currentUser


        if (currentUser == null) {

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
        // CONVERTIR MATERIALES A STRINGS
        // ====================================================

        val materials =

            currentState
                .selectedMaterials
                .map {

                    it.name
                }


        // ====================================================
        // DATOS DE LA SOLICITUD
        // ====================================================

        val solicitud =
            hashMapOf<String, Any>(

                "usuarioId" to
                        currentUser.uid,

                "direccion" to
                        currentState.location.trim(),

                "materiales" to
                        materials,

                "pesoKg" to
                        currentState.weightKg,

                "fechaRecoleccion" to
                        currentState.selectedDate.toString(),

                "estado" to
                        "PENDIENTE",

                "creadaEn" to
                        FieldValue.serverTimestamp()
            )


        // ====================================================
        // GUARDAR EN FIRESTORE
        // ====================================================

        firestore
            .collection("solicitudes")
            .add(solicitud)

            .addOnSuccessListener { documentReference ->

                // --------------------------------------------
                // Se creó correctamente
                // --------------------------------------------

                _state.value =
                    _state.value.copy(
                        isLoading = false
                    )


                println(
                    "Solicitud creada: ${documentReference.id}"
                )


                onSuccess()
            }

            .addOnFailureListener { exception ->

                // --------------------------------------------
                // Error
                // --------------------------------------------

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
    // REINICIAR FORMULARIO
    // ========================================================

    fun resetForm() {

        _state.value =
            CreateRequestState()
    }
}