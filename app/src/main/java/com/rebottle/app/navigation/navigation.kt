package com.rebottle.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rebottle.app.ui.screens.usuario.HomeUserScreen
import com.rebottle.app.ui.screens.auth.LoginScreen
import com.rebottle.app.ui.screens.auth.RegisterScreen
import com.rebottle.app.ui.screens.fundacion.HomeFundacionScreen
import com.rebottle.app.ui.screens.fundacion.ProfileFundacionScreen
import com.rebottle.app.ui.screens.fundacion.gestion.CollectionRequestsScreen
import com.rebottle.app.ui.screens.onBoarding.OnboardingOneScreen
import com.rebottle.app.ui.screens.onBoarding.OnboardingPrefs
import com.rebottle.app.ui.screens.onBoarding.OnboardingThreeScreen
import com.rebottle.app.ui.screens.onBoarding.OnboardingTwoScreen
import com.rebottle.app.ui.screens.usuario.CreateRequestScreen
import com.rebottle.app.ui.screens.usuario.SolicitudesUsuarioScreen

import com.rebottle.app.ui.screens.recolector.inicio.PantallaInicioRecolector
import com.rebottle.app.ui.screens.recolector.solicitudes.PantallaSolicitudesRecolector
import com.rebottle.app.ui.screens.recolector.solicitudes.PantallaDetalleAsignacionRecolector
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.rebottle.app.ui.screens.learn.LearnRoadmapScreen
import com.rebottle.app.ui.screens.usuario.gestion.EditRequestScreen
import com.rebottle.app.ui.screens.usuario.gestion.SolicitudDetalleScreenU

import com.rebottle.app.ui.screens.usuario.learn.ModuleOneFlowScreen
import com.rebottle.app.ui.screens.usuario.learn.ModuleTwoFlowScreen
import com.rebottle.app.ui.screens.usuario.learn.ModuleThreeFlowScreen
import com.rebottle.app.ui.screens.usuario.learn.ModuleFourFlowScreen
import com.rebottle.app.ui.screens.usuario.learn.ModuleFiveFlowScreen
import com.rebottle.app.ui.screens.usuario.learn.ModuleSixFlowScreen
import com.rebottle.app.ui.screens.usuario.learn.ModuleSevenFlowScreen

enum class AppScreens{

    //ONBOARDING REGISTER
    onboardingOne,
    onboardingTwo,
    onboardingThree,
    login,
    register,

    //FUNDACION
    homeFundacion,
    solicitudes,

    //CIUDADANO
    homeCiudadano,

    crearSolicitud,

    solicitudesUsuario,
    detallesSolicitud,
    editarSolicitud,
    moduleOne,
    moduleTwo,
    moduleThree,
    moduleFour,
    moduleFive,
    moduleSix,
    moduleSeven,


    //RECOLECTOR
    aprendizaje,
    homeRecolector,

    perfil,
    rutas,

    solicitudesRecolector,
    detalleAsignacionRecolector,
}

@Composable
fun Navigation() {

    val navController = rememberNavController()
    val context = LocalContext.current

    val startDestination = if (OnboardingPrefs.isOnboardingCompleted(context)) {
        AppScreens.login.name
    } else {
        AppScreens.onboardingOne.name
    }


    NavHost(navController = navController, startDestination = startDestination) {


        // **ONBOARDING** //
        composable(route = AppScreens.onboardingOne.name) {
            OnboardingOneScreen(navController)
        }
        composable(route = AppScreens.onboardingTwo.name) {
            OnboardingTwoScreen(navController)
        }
        composable(route = AppScreens.onboardingThree.name) {
            OnboardingThreeScreen(navController)
        }

        //**LOGIN Y REGISTER**//
        composable(route = AppScreens.login.name) {
            LoginScreen(navController)
        }
        composable(route = AppScreens.register.name){
            RegisterScreen(navController)
        }

        //**FUNDACION**//
        composable(route = AppScreens.homeFundacion.name){
            HomeFundacionScreen(navController)
        }
        composable(route = AppScreens.homeCiudadano.name) {
            HomeUserScreen(navController)

        }
        composable(route = AppScreens.solicitudes.name){
            CollectionRequestsScreen(navController)
        }

        composable(route = AppScreens.perfil.name) {
            ProfileFundacionScreen(navController)
        }
        composable(route = AppScreens.rutas.name) {
            ProfileFundacionScreen(navController)
        }



        //**CIUDADANO**/
        composable(route = AppScreens.homeCiudadano.name) {
            HomeUserScreen(navController)

        }
        composable(route = AppScreens.crearSolicitud.name){
            CreateRequestScreen(navController)
        }

        composable(route = AppScreens.solicitudesUsuario.name){
            SolicitudesUsuarioScreen(navController)
        }

        composable(
            route = "${AppScreens.detallesSolicitud.name}/{solicitudId}",
            arguments = listOf(navArgument("solicitudId") {
                type = NavType.StringType
            }
            )
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getString("solicitudId") ?: ""

            SolicitudDetalleScreenU(navController, solicitudId = id)
        }
        composable(
            route = "${AppScreens.editarSolicitud.name}/{solicitudId}",
            arguments = listOf(navArgument("solicitudId") {
                type = NavType.StringType
            }
            )
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getString("solicitudId") ?: ""

            EditRequestScreen(navController, solicitudId = id)
        }


        composable(route = AppScreens.homeRecolector.name) {
            PantallaInicioRecolector(navController)
        }
        composable(route = AppScreens.solicitudesRecolector.name) {
            PantallaSolicitudesRecolector(navController)
        }
        composable(
            route = AppScreens.aprendizaje.name
        ) {

            LearnRoadmapScreen(
                onBack = {
                    navController.popBackStack()
                },

                onM1Click = {
                    navController.navigate(
                        AppScreens.moduleOne.name
                    )
                },

                onM2Click = {
                    navController.navigate(
                        AppScreens.moduleTwo.name
                    )
                },

                onM3Click = {
                    navController.navigate(
                        AppScreens.moduleThree.name
                    )
                },

                onM4Click = {
                    navController.navigate(
                        AppScreens.moduleFour.name
                    )
                },

                onM5Click = {
                    navController.navigate(
                        AppScreens.moduleFive.name
                    )
                },

                onM6Click = {
                    navController.navigate(
                        AppScreens.moduleSix.name
                    )
                },

                onM7Click = {
                    navController.navigate(
                        AppScreens.moduleSeven.name
                    )
                }
            )
        }
        // ============================================================
// MÓDULO 1
// ============================================================

        composable(
            route = AppScreens.moduleOne.name
        ) {

            ModuleOneFlowScreen(

                onBackToRoadmap = {
                    navController.popBackStack()
                },

                onFinish = {
                    navController.popBackStack(
                        AppScreens.aprendizaje.name,
                        inclusive = false
                    )
                }
            )
        }


// ============================================================
// MÓDULO 2
// ============================================================

        composable(
            route = AppScreens.moduleTwo.name
        ) {

            ModuleTwoFlowScreen(

                onBackToRoadmap = {
                    navController.popBackStack()
                },

                onFinish = {
                    navController.popBackStack(
                        AppScreens.aprendizaje.name,
                        inclusive = false
                    )
                }
            )
        }


// ============================================================
// MÓDULO 3
// ============================================================

        composable(
            route = AppScreens.moduleThree.name
        ) {

            ModuleThreeFlowScreen(

                onBackToRoadmap = {
                    navController.popBackStack()
                },

                onFinish = {
                    navController.popBackStack(
                        AppScreens.aprendizaje.name,
                        inclusive = false
                    )
                }
            )
        }


// ============================================================
// MÓDULO 4
// ============================================================

        composable(
            route = AppScreens.moduleFour.name
        ) {

            ModuleFourFlowScreen(

                onBackToRoadmap = {
                    navController.popBackStack()
                },

                onFinish = {
                    navController.popBackStack(
                        AppScreens.aprendizaje.name,
                        inclusive = false
                    )
                }
            )
        }


// ============================================================
// MÓDULO 5
// ============================================================

        composable(
            route = AppScreens.moduleFive.name
        ) {

            ModuleFiveFlowScreen(

                onBackToRoadmap = {
                    navController.popBackStack()
                },

                onFinish = {
                    navController.popBackStack(
                        AppScreens.aprendizaje.name,
                        inclusive = false
                    )
                }
            )
        }


// ============================================================
// MÓDULO 6
// ============================================================

        composable(
            route = AppScreens.moduleSix.name
        ) {

            ModuleSixFlowScreen(

                onBackToRoadmap = {
                    navController.popBackStack()
                },

                onFinish = {
                    navController.popBackStack(
                        AppScreens.aprendizaje.name,
                        inclusive = false
                    )
                }
            )
        }


// ============================================================
// MÓDULO 7
// ============================================================

        composable(
            route = AppScreens.moduleSeven.name
        ) {

            ModuleSevenFlowScreen(

                onBackToRoadmap = {
                    navController.popBackStack()
                },

                onFinish = {
                    navController.popBackStack(
                        AppScreens.aprendizaje.name,
                        inclusive = false
                    )
                }
            )
        }
    }
}