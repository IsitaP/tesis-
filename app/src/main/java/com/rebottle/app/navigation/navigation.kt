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

enum class AppScreens{
    onboardingOne,
    onboardingTwo,
    onboardingThree,
    login,
    register,
    homeFundacion,
    homeCiudadano,

    crearSolicitud,

    solicitudesUsuario,
    homeRecolector,
    solicitudes,

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
        composable(route = AppScreens.homeRecolector.name) {
            PantallaInicioRecolector(navController)
        }
        composable(route = AppScreens.solicitudesRecolector.name) {
            PantallaSolicitudesRecolector(navController)
        }
        composable(
            route = "${AppScreens.detalleAsignacionRecolector.name}/{asignacionId}",
            arguments = listOf(navArgument("asignacionId") { type = NavType.StringType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getString("asignacionId") ?: ""
            PantallaDetalleAsignacionRecolector(navController, id)
        }
    }
}