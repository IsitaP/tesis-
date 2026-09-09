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
//import com.rebottle.app.ui.screens.fundacion.gestion.ConfirmationChangesScreen


private enum class FundacionScreen {

    HOME,

    PROFILE,

    MANAGE_REQUESTS,

    MANAGE_COLLECTORS,

    EDIT_COLLECTOR,

    CONFIRMATION_CHANGES
}


@Composable
fun FundacionFlowScreen() {


    var currentScreen by remember {
        mutableStateOf(
            FundacionScreen.HOME
        )
    }


    // ----------------------------------------------------
    // RECOLECTOR SELECCIONADO
    // ----------------------------------------------------

    var selectedCollector by remember {
        mutableStateOf<CollectorUiModel?>(null)
    }


    when (currentScreen) {


        // ------------------------------------------------
        // HOME
        // ------------------------------------------------

        FundacionScreen.HOME -> {

            HomeFundacionScreen(

                onProfileClick = {

                    currentScreen =
                        FundacionScreen.PROFILE
                }
            )
        }


        // ------------------------------------------------
        // PROFILE
        // ------------------------------------------------

        FundacionScreen.PROFILE -> {

            ProfileFundacionScreen(

                onHomeClick = {

                    currentScreen =
                        FundacionScreen.HOME
                },

                onManageRequestsClick = {

                    currentScreen =
                        FundacionScreen.MANAGE_REQUESTS
                },

                onManageCollectorsClick = {

                    currentScreen =
                        FundacionScreen.MANAGE_COLLECTORS
                }
            )
        }


        // ------------------------------------------------
        // ADMINISTRAR SOLICITUDES
        // ------------------------------------------------

        FundacionScreen.MANAGE_REQUESTS -> {

            ManageRequestsScreen(

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
                }
            )
        }


        // ------------------------------------------------
        // ADMINISTRAR RECOLECTORES
        // ------------------------------------------------

        FundacionScreen.MANAGE_COLLECTORS -> {

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


                    // Guardamos TODO el recolector
                    // que el usuario seleccionó

                    selectedCollector =
                        collector


                    currentScreen =
                        FundacionScreen.EDIT_COLLECTOR
                }
            )
        }


        // ------------------------------------------------
        // EDITAR RECOLECTOR
        // ------------------------------------------------

        FundacionScreen.EDIT_COLLECTOR -> {


            selectedCollector?.let { collector ->


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


                    // ------------------------------------
                    // ATRÁS
                    // ------------------------------------

                    onBackClick = {

                        currentScreen =
                            FundacionScreen.MANAGE_COLLECTORS
                    },


                    // ------------------------------------
                    // ACEPTAR CAMBIOS
                    // ------------------------------------

                    onAcceptClick = {

                        currentScreen =
                            FundacionScreen.CONFIRMATION_CHANGES
                    },


                    // ------------------------------------
                    // CANCELAR
                    // ------------------------------------

                    onCancelClick = {

                        currentScreen =
                            FundacionScreen.MANAGE_COLLECTORS
                    },


                    // ------------------------------------
                    // ELIMINAR
                    // ------------------------------------

                    onDeleteClick = {

                        currentScreen =
                            FundacionScreen.MANAGE_COLLECTORS
                    }
                )
            }
        }


        // ------------------------------------------------
        // CONFIRMACIÓN
        // ------------------------------------------------

        FundacionScreen.CONFIRMATION_CHANGES -> {

//            ConfirmationChangesScreen(
//
//                onContinueClick = {
//
//                    currentScreen =
//                        FundacionScreen.MANAGE_COLLECTORS
//                }
//            )
        }
    }
}