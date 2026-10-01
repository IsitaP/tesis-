package com.rebottle.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rebottle.app.ui.screens.auth.LoginScreen
import com.rebottle.app.ui.screens.auth.RegisterScreen
import com.rebottle.app.ui.screens.fundacion.HomeFundacionScreen
import com.rebottle.app.ui.screens.fundacion.ProfileFundacionScreen
import com.rebottle.app.ui.screens.fundacion.EditarPerfilFundacionScreen
import com.rebottle.app.ui.screens.fundacion.gestion.CollectionRequestsScreen
import com.rebottle.app.ui.screens.onBoarding.OnboardingOneScreen
import com.rebottle.app.ui.screens.onBoarding.OnboardingPrefs
import com.rebottle.app.ui.screens.onBoarding.OnboardingThreeScreen
import com.rebottle.app.ui.screens.onBoarding.OnboardingTwoScreen
import com.rebottle.app.ui.screens.usuario.CreateRequestScreen
import com.rebottle.app.ui.screens.usuario.EditarPerfilUsuarioScreen
import com.rebottle.app.ui.screens.usuario.inicio.HomeUserScreen
import com.rebottle.app.ui.screens.usuario.inicio.ProfileUserScreen
import com.rebottle.app.ui.screens.usuario.gamificacion.MisAportesScreen
import com.rebottle.app.ui.screens.usuario.SolicitudesUsuarioScreen
import com.rebottle.app.ui.screens.usuario.gestion.SolicitudDetalleScreenU
import com.rebottle.app.ui.screens.usuario.gestion.EditRequestScreen
import com.rebottle.app.ui.screens.learn.LearnRoadmapScreen

import com.rebottle.app.ui.screens.recolector.inicio.PantallaInicioRecolector
import com.rebottle.app.ui.screens.recolector.solicitudes.PantallaSolicitudesRecolector
import com.rebottle.app.ui.screens.recolector.solicitudes.PantallaDetalleAsignacionRecolector
import com.rebottle.app.ui.screens.recolector.perfil.PantallaPerfilRecolector
import androidx.navigation.NavType
import androidx.navigation.navArgument

import com.rebottle.app.ui.screens.usuario.learn.ModuleOneFlowScreen
import com.rebottle.app.ui.screens.usuario.learn.ModuleTwoFlowScreen
import com.rebottle.app.ui.screens.usuario.learn.ModuleThreeFlowScreen
import com.rebottle.app.ui.screens.usuario.learn.ModuleFourFlowScreen
import com.rebottle.app.ui.screens.usuario.learn.ModuleFiveFlowScreen
import com.rebottle.app.ui.screens.usuario.learn.ModuleSixFlowScreen
import com.rebottle.app.ui.screens.usuario.learn.ModuleSevenFlowScreen

enum class AppScreens {

    //ONBOARDING REGISTER
    onboardingOne,
    onboardingTwo,
    onboardingThree,
    login,
    register,

    //FUNDACION
    homeFundacion,
    editarPerfilFundacion,
    solicitudes,
    perfil,
    rutas,

    //CIUDADANO
    homeCiudadano,
    perfilUsuario,
    editarPerfilUsuario,
    crearSolicitud,
    solicitudesUsuario,
    editarSolicitud,
    detalleSolicitudUsuario,
    aprendizaje,
    misAportes,

    moduleOne,
    moduleTwo,
    moduleThree,
    moduleFour,
    moduleFive,
    moduleSix,
    moduleSeven,

    //RECOLECTOR
    homeRecolector,
    perfilRecolector,
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
        composable(route = AppScreens.onboardingOne.name) { OnboardingOneScreen(navController) }
        composable(route = AppScreens.onboardingTwo.name) { OnboardingTwoScreen(navController) }
        composable(route = AppScreens.onboardingThree.name) { OnboardingThreeScreen(navController) }

        //**LOGIN Y REGISTER**//
        composable(route = AppScreens.login.name) { LoginScreen(navController) }
        composable(route = AppScreens.register.name) { RegisterScreen(navController) }

        //**FUNDACION**//
        composable(route = AppScreens.homeFundacion.name) { HomeFundacionScreen(navController) }
        composable(route = AppScreens.solicitudes.name) { CollectionRequestsScreen(navController) }
        composable(route = AppScreens.perfil.name) { ProfileFundacionScreen(navController) }
        composable(route = AppScreens.rutas.name) { ProfileFundacionScreen(navController) }
        composable(route = AppScreens.editarPerfilFundacion.name) { EditarPerfilFundacionScreen(navController) }

        //**RECOLECTOR**//
        composable(route = AppScreens.homeRecolector.name) { PantallaInicioRecolector(navController) }
        composable(route = AppScreens.solicitudesRecolector.name) { PantallaSolicitudesRecolector(navController) }
        composable(route = AppScreens.perfilRecolector.name) { PantallaPerfilRecolector(navController) }
        composable(
            route = "${AppScreens.detalleAsignacionRecolector.name}/{asignacionId}",
            arguments = listOf(navArgument("asignacionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("asignacionId") ?: ""
            PantallaDetalleAsignacionRecolector(navController, id)
        }

        //**CIUDADANO**//
        composable(route = AppScreens.homeCiudadano.name) { HomeUserScreen(navController) }
        composable(route = AppScreens.perfilUsuario.name) { ProfileUserScreen(navController) }
        composable(route = AppScreens.editarPerfilUsuario.name) { EditarPerfilUsuarioScreen(navController) }
        composable(route = AppScreens.crearSolicitud.name) { CreateRequestScreen(navController) }
        composable(route = AppScreens.solicitudesUsuario.name) { SolicitudesUsuarioScreen(navController) }
        composable(route = AppScreens.misAportes.name) { MisAportesScreen(controller = navController) }

        composable(
            route = "${AppScreens.detalleSolicitudUsuario.name}/{solicitudId}",
            arguments = listOf(navArgument("solicitudId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("solicitudId") ?: ""
            SolicitudDetalleScreenU(navController, solicitudId = id)
        }

        composable(
            route = "${AppScreens.editarSolicitud.name}/{solicitudId}",
            arguments = listOf(navArgument("solicitudId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("solicitudId") ?: ""
            EditRequestScreen(navController, solicitudId = id)
        }

        composable(route = AppScreens.aprendizaje.name) {
            LearnRoadmapScreen(
                onBack = { navController.popBackStack() },
                onM1Click = { navController.navigate(AppScreens.moduleOne.name) },
                onM2Click = { navController.navigate(AppScreens.moduleTwo.name) },
                onM3Click = { navController.navigate(AppScreens.moduleThree.name) },
                onM4Click = { navController.navigate(AppScreens.moduleFour.name) },
                onM5Click = { navController.navigate(AppScreens.moduleFive.name) },
                onM6Click = { navController.navigate(AppScreens.moduleSix.name) },
                onM7Click = { navController.navigate(AppScreens.moduleSeven.name) }
            )
        }

        composable(route = AppScreens.moduleOne.name) {
            ModuleOneFlowScreen(
                onBackToRoadmap = { navController.popBackStack() },
                onFinish = { navController.popBackStack(AppScreens.aprendizaje.name, inclusive = false) }
            )
        }
        composable(route = AppScreens.moduleTwo.name) {
            ModuleTwoFlowScreen(
                onBackToRoadmap = { navController.popBackStack() },
                onFinish = { navController.popBackStack(AppScreens.aprendizaje.name, inclusive = false) }
            )
        }
        composable(route = AppScreens.moduleThree.name) {
            ModuleThreeFlowScreen(
                onBackToRoadmap = { navController.popBackStack() },
                onFinish = { navController.popBackStack(AppScreens.aprendizaje.name, inclusive = false) }
            )
        }
        composable(route = AppScreens.moduleFour.name) {
            ModuleFourFlowScreen(
                onBackToRoadmap = { navController.popBackStack() },
                onFinish = { navController.popBackStack(AppScreens.aprendizaje.name, inclusive = false) }
            )
        }
        composable(route = AppScreens.moduleFive.name) {
            ModuleFiveFlowScreen(
                onBackToRoadmap = { navController.popBackStack() },
                onFinish = { navController.popBackStack(AppScreens.aprendizaje.name, inclusive = false) }
            )
        }
        composable(route = AppScreens.moduleSix.name) {
            ModuleSixFlowScreen(
                onBackToRoadmap = { navController.popBackStack() },
                onFinish = { navController.popBackStack(AppScreens.aprendizaje.name, inclusive = false) }
            )
        }
        composable(route = AppScreens.moduleSeven.name) {
            ModuleSevenFlowScreen(
                onBackToRoadmap = { navController.popBackStack() },
                onFinish = { navController.popBackStack(AppScreens.aprendizaje.name, inclusive = false) }
            )
        }
    }
}