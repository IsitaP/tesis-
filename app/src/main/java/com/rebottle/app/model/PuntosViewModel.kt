package com.rebottle.app.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import com.rebottle.app.ui.screens.usuario.gamificacion.SlotQuis
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow






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

    // Puntos que puede gastar
    val puntosDisponibles: Long = 0L,

    val modulosCompletados: List<ModuloCompletado> = emptyList(),

    // IDs de ropa/accesorios comprados
    val itemsComprados: Set<String> = emptySet(),

    // Slot -> id del item
    val itemsEquipados: Map<String, String> = emptyMap(),

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

    companion object {
        private const val TAG = "PUNTOS"
    }

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

        Log.d(TAG, "completarModulo($moduloId) llamado")

        val usuarioActual = auth.currentUser ?: run {
            Log.e(TAG, "No hay usuario autenticado: no se guardan puntos")
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

                if (PuntosModulos.puntosASumar(moduloId, completados.keys) == 0L) {
                    // Ya lo había completado antes: no suma
                    return@runTransaction 0L
                }

                transaction.set(
                    ref,
                    mapOf(
                        "usuarioId" to usuarioActual.uid,
                        "puntosTotales" to FieldValue.increment(puntos),
                        "puntosDisponibles" to FieldValue.increment(puntos),
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
                Log.d(TAG, "Módulo $moduloId guardado. Puntos sumados: $sumados (0 = ya estaba completado)")
                onResult(sumados)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error guardando puntos de $moduloId", e)
                _state.value = _state.value.copy(
                    error = e.message ?: "Error al guardar los puntos"
                )
            }
    }
    fun comprarItem(
        itemId: String,
        precio: Long,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {

        val usuarioActual = auth.currentUser ?: run {
            onError("No hay usuario autenticado")
            return
        }

        val ref =
            progresoRef(usuarioActual.uid)

        firestore
            .runTransaction { transaction ->

                val snapshot =
                    transaction.get(ref)

                val puntosDisponibles =
                    snapshot.getLong(
                        "puntosDisponibles"
                    )
                        ?: snapshot.getLong(
                            "puntosTotales"
                        )
                        ?: 0L

                @Suppress("UNCHECKED_CAST")
                val itemsComprados =
                    snapshot.get(
                        "itemsComprados"
                    ) as? List<String>
                        ?: emptyList()

                // Ya lo compró
                if (itemId in itemsComprados) {
                    return@runTransaction
                }

                if (puntosDisponibles < precio) {
                    throw Exception(
                        "No tienes suficientes puntos"
                    )
                }

                transaction.update(
                    ref,
                    mapOf(
                        "puntosDisponibles" to
                                puntosDisponibles - precio,

                        "itemsComprados" to
                                FieldValue.arrayUnion(itemId),

                        "actualizadoEn" to
                                FieldValue.serverTimestamp()
                    )
                )
            }
            .addOnSuccessListener {

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "Error al comprar el artículo"
                )
            }
    }
    fun equiparItem(
        slot: SlotQuis,
        itemId: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {

        val usuarioActual = auth.currentUser ?: run {
            onError("No hay usuario autenticado")
            return
        }

        progresoRef(usuarioActual.uid)
            .update(
                "itemsEquipados.${slot.name}",
                itemId
            )
            .addOnSuccessListener {

                onSuccess()
            }
            .addOnFailureListener { exception ->

                onError(
                    exception.message
                        ?: "Error al equipar el artículo"
                )
            }
    }
    fun desequiparItem(
        slot: SlotQuis
    ) {

        val usuarioActual =
            auth.currentUser ?: return

        progresoRef(usuarioActual.uid)
            .update(
                "itemsEquipados.${slot.name}",
                FieldValue.delete()
            )
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
                        Log.e(TAG, "Error leyendo puntos", error)
                        _state.value = _state.value.copy(
                            isLoading = false,
                            error = error.message
                        )
                        return@addSnapshotListener
                    }

                    val total =
                        snapshot?.getLong("puntosTotales") ?: 0L
                    val disponibles =
                        snapshot?.getLong(
                            "puntosDisponibles"
                        ) ?: total
                    @Suppress("UNCHECKED_CAST")
                    val comprados =
                        snapshot?.get(
                            "itemsComprados"
                        ) as? List<String>
                            ?: emptyList()
                    @Suppress("UNCHECKED_CAST")
                    val equipados =
                        snapshot?.get(
                            "itemsEquipados"
                        ) as? Map<String, String>
                            ?: emptyMap()

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
                        puntosDisponibles = disponibles,
                        itemsComprados = comprados.toSet(),
                        itemsEquipados = equipados,
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
