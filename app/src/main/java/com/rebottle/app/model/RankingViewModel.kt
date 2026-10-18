package com.rebottle.app.model

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.rebottle.app.ui.screens.auth.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Calendar


// ========================================================
// MODELOS
// ========================================================

data class UsuarioRanking(
    val puesto: Int,
    val uid: String,
    val nombre: String,
    val puntos: Int,
    val fotoUrl: String = ""
)

data class RankingState(
    val semanal: List<UsuarioRanking> = emptyList(),
    val historico: List<UsuarioRanking> = emptyList(),
    val uidActual: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)


// ========================================================
// VIEWMODEL
//
// Junta dos colecciones en tiempo real:
//   users (role = ciudadano)  -> nombre y foto
//   progresoAprendizaje/{uid} -> puntosTotales y
//                                fecha de cada módulo
//
// Histórico: puntosTotales
// Semanal:   puntos de los módulos completados desde
//            el lunes de esta semana
// ========================================================

class RankingViewModel : ViewModel() {

    companion object {
        private const val TAG = "RANKING"
    }

    private data class Perfil(
        val nombre: String,
        val fotoUrl: String
    )

    private data class Progreso(
        val total: Long,
        val modulos: Map<String, Map<String, Any?>>
    )

    private val firestore = FirebaseFirestore.getInstance()

    private val _state = MutableStateFlow(RankingState())
    val state: StateFlow<RankingState> = _state.asStateFlow()

    private var perfiles: Map<String, Perfil> = emptyMap()
    private var progresos: Map<String, Progreso> = emptyMap()

    private var perfilesCargados = false
    private var progresosCargados = false

    private var usersListener: ListenerRegistration? = null
    private var progresoListener: ListenerRegistration? = null


    fun escucharRanking() {

        if (usersListener != null) return

        _state.value = _state.value.copy(
            uidActual = FirebaseAuth.getInstance().currentUser?.uid
        )

        usersListener =
            firestore
                .collection("perfilesPublicos")
                .whereEqualTo("role", UserRole.CIUDADANO.firestoreValue)
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        Log.e(TAG, "Error leyendo usuarios", error)
                        _state.value = _state.value.copy(isLoading = false, error = error.message)
                        return@addSnapshotListener
                    }

                    perfiles =
                        snapshot?.documents.orEmpty().associate { doc ->
                            doc.id to Perfil(
                                nombre = doc.getString("name").orEmpty().ifBlank { "Usuario" },
                                fotoUrl = doc.getString("fotoUrl").orEmpty()
                            )
                        }

                    perfilesCargados = true
                    recalcular()
                }

        progresoListener =
            firestore
                .collection("progresoAprendizaje")
                .addSnapshotListener { snapshot, error ->

                    if (error != null) {
                        Log.e(TAG, "Error leyendo progreso", error)
                        _state.value = _state.value.copy(isLoading = false, error = error.message)
                        return@addSnapshotListener
                    }

                    progresos =
                        snapshot?.documents.orEmpty().associate { doc ->
                            @Suppress("UNCHECKED_CAST")
                            doc.id to Progreso(
                                total = doc.getLong("puntosTotales") ?: 0L,
                                modulos = doc.get("modulosCompletados") as? Map<String, Map<String, Any?>>
                                    ?: emptyMap()
                            )
                        }

                    progresosCargados = true
                    recalcular()
                }
    }


    private fun recalcular() {

        // Espera a tener las dos colecciones para no
        // mostrar un ranking a medias
        if (!perfilesCargados || !progresosCargados) return

        val inicioSemana = inicioDeSemana()

        val semanal = mutableMapOf<String, Int>()
        val historico = mutableMapOf<String, Int>()

        perfiles.keys.forEach { uid ->

            val progreso = progresos[uid]

            historico[uid] = (progreso?.total ?: 0L).toInt()

            semanal[uid] =
                progreso?.modulos?.values.orEmpty()
                    .filter { datos ->
                        val fecha = datos["fecha"] as? Timestamp
                        fecha != null && fecha.toDate().time >= inicioSemana
                    }
                    .sumOf { datos -> (datos["puntos"] as? Number)?.toInt() ?: 0 }
        }

        _state.value = _state.value.copy(
            semanal = ordenar(semanal),
            historico = ordenar(historico),
            isLoading = false,
            error = null
        )
    }


    // Mayor puntaje primero. Si empatan comparten puesto
    // (ej: 1, 2, 2, 4) y se ordenan por nombre.
    private fun ordenar(puntos: Map<String, Int>): List<UsuarioRanking> {

        val ordenados =
            puntos.entries.sortedWith(
                compareByDescending<Map.Entry<String, Int>> { it.value }
                    .thenBy { perfiles[it.key]?.nombre?.lowercase() }
            )

        var puestoAnterior = 0
        var puntosAnteriores: Int? = null

        return ordenados.mapIndexed { index, (uid, pts) ->

            val puesto =
                if (pts == puntosAnteriores) puestoAnterior else index + 1

            puestoAnterior = puesto
            puntosAnteriores = pts

            val perfil = perfiles[uid]

            UsuarioRanking(
                puesto = puesto,
                uid = uid,
                nombre = perfil?.nombre ?: "Usuario",
                puntos = pts,
                fotoUrl = perfil?.fotoUrl.orEmpty()
            )
        }
    }


    // Lunes de esta semana a las 00:00 (hora del celular)
    private fun inicioDeSemana(): Long =
        Calendar.getInstance().apply {
            firstDayOfWeek = Calendar.MONDAY
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        }.timeInMillis


    override fun onCleared() {
        usersListener?.remove()
        progresoListener?.remove()
        super.onCleared()
    }
}
