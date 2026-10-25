package com.rebottle.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rebottle.app.ui.screens.auth.LoginScreen
import com.rebottle.app.ui.screens.auth.RegisterScreen
import com.rebottle.app.ui.screens.fundacion.HomeFundacionScreen
import com.rebottle.app.ui.screens.fundacion.ProfileFundacionScreen
import com.rebottle.app.ui.screens.fundacion.EditarPerfilFundacionScreen
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
import com.rebottle.app.ui.screens.usuario.gamificacion.RecompensasFlowScreen
import com.rebottle.app.ui.screens.usuario.gamificacion.RankingScreen

import com.rebottle.app.ui.screens.recolector.inicio.PantallaInicioRecolector
import com.rebottle.app.ui.screens.recolector.solicitudes.PantallaSolicitudesRecolector
import com.rebottle.app.ui.screens.recolector.solicitudes.PantallaDetalleAsignacionRecolector
import com.rebottle.app.ui.screens.recolector.perfil.PantallaPerfilRecolector
import com.rebottle.app.ui.screens.recolector.rutas.MapaRutaRecolectorScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.rebottle.app.ui.screens.fundacion.gestion.RutasCreadasScreen
import com.rebottle.app.ui.screens.fundacion.solicitudes.RutaDetalleScreen
import com.rebottle.app.ui.screens.fundacion.rutas.RutasRecolectoresScreen
import com.rebottle.app.ui.screens.fundacion.rutas.DetalleRecolectorScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rebottle.app.model.PuntosModulos
import com.rebottle.app.model.PuntosViewModel
import com.rebottle.app.model.RutasFundacionViewModel
import com.rebottle.app.ui.screens.fundacion.rutas.FiltroRutasScreen
import com.rebottle.app.ui.screens.fundacion.gestion.AsignarRecolectorScreen


import com.rebottle.app.ui.screens.usuario.learn.ModuleOneFlowScreen
import com.rebottle.app.ui.screens.usuario.learn.ModuleTwoFlowScreen
import com.rebottle.app.ui.screens.usuario.learn.ModuleThreeFlowScreen
import com.rebottle.app.ui.screens.usuario.learn.ModuleFourFlowScreen
import com.rebottle.app.ui.screens.usuario.learn.ModuleFiveFlowScreen
import com.rebottle.app.ui.screens.usuario.learn.ModuleSixFlowScreen
import com.rebottle.app.ui.screens.usuario.learn.ModuleSevenFlowScreen
import com.rebottle.app.ui.screens.usuario.confirmaciones.ConfirmacionSolicitudUsuarioScreen
import com.rebottle.app.ui.screens.usuario.confirmaciones.RechazoSolicitudUsuario

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
    solicitudesFundacion,
    detalleRuta,
    asignarRecolector,
    perfil,
    rutas,
    detalleRecolectorRuta,
    detalleParadaRecolector,
    filtroRutas,
    aportesFundacion,

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
    recompensas,
    ranking,

    confirmacionSolicitud,
    rechazoSolicitud,

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
    rutasRecolector,
    detalleAsignacionRecolector,
    mapaRecolector
}

@Composable
fun Navigation() {

    val navController = rememberNavController()
    val context = LocalContext.current

    // ViewModel de puntos compartido (módulos + Recompensas)
    val puntosViewModel: PuntosViewModel = viewModel()
    // Compartido entre la lista de rutas y la pantalla de filtros
    val rutasFundacionViewModel: RutasFundacionViewModel = viewModel()

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
        composable(route = AppScreens.solicitudesFundacion.name) { RutasCreadasScreen(navController) }
        composable(route = AppScreens.perfil.name) { ProfileFundacionScreen(navController) }
        composable(route = AppScreens.rutas.name) {
            RutasRecolectoresScreen(navController, model = rutasFundacionViewModel)
        }

        composable(route = AppScreens.filtroRutas.name) {
            FiltroRutasScreen(navController, model = rutasFundacionViewModel)
        }
        composable(
            route = "${AppScreens.detalleRecolectorRuta.name}/{recolectorId}",
            arguments = listOf(navArgument("recolectorId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("recolectorId") ?: ""
            DetalleRecolectorScreen(navController, recolectorId = id)
        }
        // Detalle de una solicitud de la ruta de un recolector (solo lectura)
        composable(
            route = "${AppScreens.detalleParadaRecolector.name}/{recolectorId}/{solicitudId}",
            arguments = listOf(
                navArgument("recolectorId") { type = NavType.StringType },
                navArgument("solicitudId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val recolectorId = backStackEntry.arguments?.getString("recolectorId") ?: ""
            val solicitudId = backStackEntry.arguments?.getString("solicitudId") ?: ""
            PantallaDetalleAsignacionRecolector(
                navController,
                asignacionId = solicitudId,
                recolectorId = recolectorId,
                soloLectura = true
            )
        }
        composable(route = AppScreens.editarPerfilFundacion.name) { EditarPerfilFundacionScreen(navController) }
        composable(route = AppScreens.aportesFundacion.name) {
            MisAportesScreen(controller = navController, deFundacion = true)
        }

        composable(
            route = "${AppScreens.detalleRuta.name}/{rutaId}",
            arguments = listOf(navArgument("rutaId") {type = NavType.StringType }
            )
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getString("rutaId") ?: ""

            RutaDetalleScreen(
                rutaId = id,
                navController = navController
            )
        }

        composable(
            route = "${AppScreens.asignarRecolector.name}/{rutaId}",
            arguments = listOf(navArgument("rutaId") {type = NavType.StringType }
            )
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getString("rutaId") ?: ""

            AsignarRecolectorScreen(controller = navController, rutaId = id,)
        }


        //**RECOLECTOR**//
        composable(route = AppScreens.homeRecolector.name) { PantallaInicioRecolector(navController) }
        composable(route = AppScreens.solicitudesRecolector.name) { PantallaSolicitudesRecolector(navController) }
        composable(route = AppScreens.perfilRecolector.name) { PantallaPerfilRecolector(navController) }
        composable(route = AppScreens.rutasRecolector.name) { MapaRutaRecolectorScreen(controller = navController) }
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
       // composable(route = AppScreens.confirmacionSolicitud.name) { ConfirmacionSolicitudUsuarioScreen(navController) }
        composable(route = AppScreens.rechazoSolicitud.name) { RechazoSolicitudUsuario(navController) }


        composable(
            route = "${AppScreens.confirmacionSolicitud.name}/{solicitudId}",
            arguments = listOf(navArgument("solicitudId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("solicitudId") ?: ""
            ConfirmacionSolicitudUsuarioScreen(navController, solicitudId = id)
        }


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
            // Módulos completados desde Firebase para bloquear la ruta
            val puntosState by puntosViewModel.state.collectAsState()
            LaunchedEffect(Unit) { puntosViewModel.escucharPuntos() }

            LearnRoadmapScreen(
                modulosCompletados = puntosState.modulosCompletados.map { it.moduloId }.toSet(),
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
                onFinish = { navController.popBackStack(AppScreens.aprendizaje.name, inclusive = false) },
                onModuleCompleted = { puntosViewModel.completarModulo(PuntosModulos.M1) }
            )
        }
        composable(route = AppScreens.moduleTwo.name) {
            ModuleTwoFlowScreen(
                onBackToRoadmap = { navController.popBackStack() },
                onFinish = { navController.popBackStack(AppScreens.aprendizaje.name, inclusive = false) },
                onModuleCompleted = { puntosViewModel.completarModulo(PuntosModulos.M2) }
            )
        }
        composable(route = AppScreens.moduleThree.name) {
            ModuleThreeFlowScreen(
                onBackToRoadmap = { navController.popBackStack() },
                onFinish = { navController.popBackStack(AppScreens.aprendizaje.name, inclusive = false) },
                onModuleCompleted = { puntosViewModel.completarModulo(PuntosModulos.M3) }
            )
        }
        composable(route = AppScreens.moduleFour.name) {
            ModuleFourFlowScreen(
                onBackToRoadmap = { navController.popBackStack() },
                onFinish = { navController.popBackStack(AppScreens.aprendizaje.name, inclusive = false) },
                onModuleCompleted = { puntosViewModel.completarModulo(PuntosModulos.M4) }
            )
        }
        composable(route = AppScreens.moduleFive.name) {
            ModuleFiveFlowScreen(
                onBackToRoadmap = { navController.popBackStack() },
                onFinish = { navController.popBackStack(AppScreens.aprendizaje.name, inclusive = false) },
                onModuleCompleted = { puntosViewModel.completarModulo(PuntosModulos.M5) }
            )
        }
        composable(route = AppScreens.moduleSix.name) {
            ModuleSixFlowScreen(
                onBackToRoadmap = { navController.popBackStack() },
                onFinish = { navController.popBackStack(AppScreens.aprendizaje.name, inclusive = false) },
                onModuleCompleted = { puntosViewModel.completarModulo(PuntosModulos.M6) }
            )
        }
        composable(
            route = AppScreens.recompensas.name
        ) {

            RecompensasFlowScreen(
                controller = navController,
                puntosViewModel = puntosViewModel
            )
        }
        composable(route = AppScreens.ranking.name) {
            RankingScreen(controller = navController)
        }
        composable(route = AppScreens.moduleSeven.name) {
            ModuleSevenFlowScreen(
                onBackToRoadmap = { navController.popBackStack() },
                onFinish = { navController.popBackStack(AppScreens.aprendizaje.name, inclusive = false) },
                onModuleCompleted = { puntosViewModel.completarModulo(PuntosModulos.M7) }
            )
        }
    }
}