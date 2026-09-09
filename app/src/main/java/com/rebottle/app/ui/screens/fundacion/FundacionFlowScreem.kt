package com.rebottle.app.ui.screens.fundacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

import com.rebottle.app.ui.screens.fundacion.gestion.CollectorUiModel
import com.rebottle.app.ui.screens.fundacion.gestion.EditCollectorScreen
import com.rebottle.app.ui.screens.fundacion.gestion.ManageCollectorsScreen
import com.rebottle.app.ui.screens.fundacion.gestion.ManageRequestsScreen
import com.rebottle.app.ui.screens.CollectionDetailScreen
import com.rebottle.app.ui.screens.fundacion.gestion.RequestUiModel


// ----------------------------------------------------
// PANTALLAS DEL FLUJO FUNDACIÓN
// ----------------------------------------------------

private enum class FundacionScreen {

    HOME,

    PROFILE,

    MANAGE_REQUESTS,

    MANAGE_COLLECTORS,

    EDIT_COLLECTOR,
    EDIT_REQUEST
}


// ----------------------------------------------------
// FLOW FUNDACIÓN
// ----------------------------------------------------

@Composable
fun FundacionFlowScreen() {


    // ------------------------------------------------
    // PANTALLA ACTUAL
    // ------------------------------------------------

    var currentScreen by remember {

        mutableStateOf(
            FundacionScreen.HOME
        )
    }

    var selectedCollector by remember {

        mutableStateOf<CollectorUiModel?>(
            null
        )
    }

    var selectedRequest by remember {
        mutableStateOf<RequestUiModel?>(null)
    }

    // ------------------------------------------------
    // NAVEGACIÓN
    // ------------------------------------------------

    when (currentScreen) {


        // ====================================================
        // HOME
        // ====================================================

        FundacionScreen.HOME -> {


            HomeFundacionScreen(

                // ----------------------------------------
                // PERFIL
                // ----------------------------------------

                onProfileClick = {

                    currentScreen =
                        FundacionScreen.PROFILE
                },


                // ----------------------------------------
                // SOLICITUDES
                // ----------------------------------------

                onRequestsClick = {

                    currentScreen =
                        FundacionScreen.MANAGE_REQUESTS
                },


                // ----------------------------------------
                // EDITAR SOLICITUD
                // ----------------------------------------
                //
                // Por ahora lleva al listado.
                //
                // Después podremos mandarlo directamente
                // a una solicitud específica.
                //
                // ----------------------------------------

                onEditRequestClick = {

                    currentScreen =
                        FundacionScreen.MANAGE_REQUESTS
                },


                // ----------------------------------------
                // EDITAR RECOLECTOR
                // ----------------------------------------
                //
                // Primero mostramos la lista para que
                // la Fundación elija cuál editar.
                //
                // ----------------------------------------

                onEditCollectorClick = {

                    currentScreen =
                        FundacionScreen.MANAGE_COLLECTORS
                },



            )
        }


        // ====================================================
        // PROFILE
        // ====================================================

        FundacionScreen.PROFILE -> {


            ProfileFundacionScreen(


                // ----------------------------------------
                // INICIO DE LA BARRA
                // ----------------------------------------

                onHomeClick = {

                    currentScreen =
                        FundacionScreen.HOME
                },


                // ----------------------------------------
                // SOLICITUDES DE LA BARRA
                // ----------------------------------------

                onSolicitudesClick = {

                    currentScreen =
                        FundacionScreen.MANAGE_REQUESTS
                },


                // ----------------------------------------
                // RUTAS
                // ----------------------------------------

                onRutasClick = {

                    // Más adelante
                },


                // ----------------------------------------
                // BOTÓN ADMINISTRAR SOLICITUDES
                // ----------------------------------------

                onManageRequestsClick = {

                    currentScreen =
                        FundacionScreen.MANAGE_REQUESTS
                },


                // ----------------------------------------
                // BOTÓN ADMINISTRAR RECOLECTORES
                // ----------------------------------------

                onManageCollectorsClick = {

                    currentScreen =
                        FundacionScreen.MANAGE_COLLECTORS
                }
            )
        }


        // ====================================================
        // ADMINISTRAR SOLICITUDES
        // ====================================================


        FundacionScreen.MANAGE_REQUESTS -> {

            ManageRequestsScreen(

                onBackClick = {
                    currentScreen = FundacionScreen.PROFILE
                },

                onHomeClick = {
                    currentScreen = FundacionScreen.HOME
                },

                onProfileClick = {
                    currentScreen = FundacionScreen.PROFILE
                },

                onEditClick = { request ->

                    selectedRequest = request

                    currentScreen =
                        FundacionScreen.EDIT_REQUEST
                }
            )
        }


        // ====================================================
        // ADMINISTRAR RECOLECTORES
        // ====================================================

        FundacionScreen.MANAGE_COLLECTORS -> {


            ManageCollectorsScreen(


                // ----------------------------------------
                // FLECHA ATRÁS
                // ----------------------------------------

                onBackClick = {

                    currentScreen =
                        FundacionScreen.PROFILE
                },


                // ----------------------------------------
                // INICIO
                // ----------------------------------------

                onHomeClick = {

                    currentScreen =
                        FundacionScreen.HOME
                },


                // ----------------------------------------
                // PERFIL
                // ----------------------------------------

                onProfileClick = {

                    currentScreen =
                        FundacionScreen.PROFILE
                },


                // ----------------------------------------
                // EDITAR RECOLECTOR
                // ----------------------------------------

                onEditClick = { collector ->


                    // Guardamos TODO el CollectorUiModel

                    selectedCollector =
                        collector


                    // Vamos a editar

                    currentScreen =
                        FundacionScreen.EDIT_COLLECTOR
                }
            )
        }

        FundacionScreen.EDIT_REQUEST -> {

            selectedRequest?.let { request ->

                CollectionDetailScreen(

                    onBackClick = {
                        currentScreen =
                            FundacionScreen.MANAGE_REQUESTS
                    },

                    onAcceptChanges = {
                        currentScreen =
                            FundacionScreen.MANAGE_REQUESTS
                    },

                    onCancelChanges = {
                        currentScreen =
                            FundacionScreen.MANAGE_REQUESTS
                    }
                )
            }
        }


        // ====================================================
        // EDITAR RECOLECTOR
        // ====================================================

        FundacionScreen.EDIT_COLLECTOR -> {

            val collector =
                selectedCollector


            if (collector != null) {


                EditCollectorScreen(

                    collectorId =
                        collector.id,

                    collectorName =
                        collector.name,

                    zone =
                        collector.zone,

                    schedule =
                        collector.schedule,

                    status =
                        collector.status,

                    onBackClick = {

                        currentScreen =
                            FundacionScreen.MANAGE_COLLECTORS
                    },



                    onAcceptClick = {


                        /*
                         * MÁS ADELANTE:
                         *
                         * 1. Actualizamos Firebase
                         * 2. Vamos a ConfirmationChangesScreen
                         *
                         * Por ahora volvemos al listado.
                         */


                        currentScreen =
                            FundacionScreen.MANAGE_COLLECTORS
                    },


                    // ----------------------------------------
                    // CANCELAR CAMBIOS
                    // ----------------------------------------

                    onCancelClick = {

                        currentScreen =
                            FundacionScreen.MANAGE_COLLECTORS
                    },


                    // ----------------------------------------
                    // ELIMINAR RECOLECTOR
                    // ----------------------------------------

                    onDeleteClick = {


                        /*
                         * Más adelante aquí:
                         *
                         * Firebase
                         *     .delete collector
                         */


                        currentScreen =
                            FundacionScreen.MANAGE_COLLECTORS
                    }
                )


            } else {
                ManageCollectorsScreen(

                    onBackClick = {

                        currentScreen =
                            FundacionScreen.PROFILE
                    },

                    onHomeClick = {

                        currentScreen =
                            FundacionScreen.HOME
                    },

                    onProfileClick = {

                        currentScreen =
                            FundacionScreen.PROFILE
                    },

                    onEditClick = { collector ->

                        selectedCollector =
                            collector

                        currentScreen =
                            FundacionScreen.EDIT_COLLECTOR
                    }
                )
            }
        }
    }
}