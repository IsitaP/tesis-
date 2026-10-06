package com.rebottle.app.model

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
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

                .whereEqualTo(
                    "estado",
                    EstadoRuta.PUBLICADA.name
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
}