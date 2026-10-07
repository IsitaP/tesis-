package com.rebottle.app.model

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow




object PuntosModulos {

    const val M1 = "M1"
    const val M2 = "M2"
    const val M3 = "M3"
    const val M4 = "M4"
    const val M5 = "M5"
    const val M6 = "M6"
    const val M7 = "M7"

    val valores: Map<String, Long> = mapOf(
        M1 to 100L,
        M2 to 150L,
        M3 to 50L,
        M4 to 200L,
        M5 to 100L,
        M6 to 50L,
        M7 to 150L
    )
}


// ========================================================
// MODELOS
// ========================================================

data class ModuloCompletado(
    val moduloId: String = "",
    val puntos: Long = 0L,
    val fecha: Timestamp? = null
)

data class PuntosState(
    val puntosTotales: Long = 0L,
    val modulosCompletados: List<ModuloCompletado> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)


// ========================================================
// VIEWMODEL
//
// Estructura en Firestore:
//
// progresoAprendizaje/{uid}
//    usuarioId: "uid"
//    puntosTotales: 450
//    actualizadoEn: timestamp
//    modulosCompletados: {
//        M1: { puntos: 100, fecha: timestamp },
//        M2: { puntos: 150, fecha: timestamp }
//    }
// ========================================================

class PuntosViewModel : ViewModel() {

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()

    private val _state =
        MutableStateFlow(PuntosState())

    val state: StateFlow<PuntosState> =
        _state.asStateFlow()

    private var puntosListener: ListenerRegistration? = null


    private fun progresoRef(uid: String) =
        firestore
            .collection("progresoAprendizaje")
            .document(uid)


    // ====================================================
    // SUMAR PUNTOS AL COMPLETAR UN MÓDULO
    //
    // Es idempotente: si el módulo ya estaba completado
    // no vuelve a sumar (se puede repetir el módulo sin
    // inflar los puntos).
    // ====================================================

    fun completarModulo(
        moduloId: String,
        onResult: (puntosSumados: Long) -> Unit = {}
    ) {

        val usuarioActual = auth.currentUser ?: run {
            _state.value = _state.value.copy(error = "No hay un usuario autenticado")
            return
        }

        val puntos = PuntosModulos.valores[moduloId] ?: run {
            _state.value = _state.value.copy(error = "Módulo desconocido: $moduloId")
            return
        }

        val ref = progresoRef(usuarioActual.uid)

        firestore
            .runTransaction { transaction ->

                val snapshot = transaction.get(ref)

                @Suppress("UNCHECKED_CAST")
                val completados =
                    snapshot.get("modulosCompletados") as? Map<String, Any>
                        ?: emptyMap()

                if (completados.containsKey(moduloId)) {
                    // Ya lo había completado antes: no suma
                    return@runTransaction 0L
                }

                transaction.set(
                    ref,
                    mapOf(
                        "usuarioId" to usuarioActual.uid,
                        "puntosTotales" to FieldValue.increment(puntos),
                        "actualizadoEn" to FieldValue.serverTimestamp(),
                        "modulosCompletados" to mapOf(
                            moduloId to mapOf(
                                "puntos" to puntos,
                                "fecha" to FieldValue.serverTimestamp()
                            )
                        )
                    ),
                    SetOptions.merge()
                )

                puntos
            }
            .addOnSuccessListener { sumados ->
                onResult(sumados)
            }
            .addOnFailureListener { e ->
                _state.value = _state.value.copy(
                    error = e.message ?: "Error al guardar los puntos"
                )
            }
    }


    // ====================================================
    // ESCUCHAR PUNTOS EN TIEMPO REAL
    // (para la pantalla de puntos: llamar en un
    //  LaunchedEffect(Unit) { puntosViewModel.escucharPuntos() })
    // ====================================================

    fun escucharPuntos() {

        val usuarioActual = auth.currentUser ?: return

        puntosListener?.remove()

        _state.value = _state.value.copy(isLoading = true)

        puntosListener =
            progresoRef(usuarioActual.uid)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = error.message
                        )
                        return@addSnapshotListener
                    }

                    val total =
                        snapshot?.getLong("puntosTotales") ?: 0L

                    @Suppress("UNCHECKED_CAST")
                    val mapa =
                        snapshot?.get("modulosCompletados") as? Map<String, Map<String, Any?>>
                            ?: emptyMap()

                    val modulos =
                        mapa.map { (id, datos) ->
                            ModuloCompletado(
                                moduloId = id,
                                puntos = (datos["puntos"] as? Number)?.toLong() ?: 0L,
                                fecha = datos["fecha"] as? Timestamp
                            )
                        }.sortedBy { it.moduloId }

                    _state.value = _state.value.copy(
                        puntosTotales = total,
                        modulosCompletados = modulos,
                        isLoading = false,
                        error = null
                    )
                }
    }


    fun estaCompletado(moduloId: String): Boolean =
        _state.value.modulosCompletados.any { it.moduloId == moduloId }


    override fun onCleared() {
        puntosListener?.remove()
        super.onCleared()
    }
}
