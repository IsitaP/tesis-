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


enum class AppScreens{
    onboardingOne,
    onboardingTwo,
    onboardingThree,
    login,
    register,
    homeFundacion,
    homeCiudadano,
    homeRecolector,
    solicitudes,
    perfil,
    rutas
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
        composable(route = AppScreens.onboardingOne.name) {
            OnboardingOneScreen(navController)
        }
        composable(route = AppScreens.onboardingTwo.name) {
            OnboardingTwoScreen(navController)
        }
        composable(route = AppScreens.onboardingThree.name) {
            OnboardingThreeScreen(navController)
        }
        composable(route = AppScreens.login.name) {
            LoginScreen(navController)
        }
        composable(route = AppScreens.register.name){
            RegisterScreen(navController)
        }
        composable(route = AppScreens.homeFundacion.name){
            HomeFundacionScreen(navController)
        }
        composable(route = AppScreens.homeCiudadano.name) {
            HomeUserScreen(
                onLearnClick = {
                    // TODO: navegar a LearnRoadmapScreen cuando tenga su ruta en el enum
                }
            )
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
    }
}