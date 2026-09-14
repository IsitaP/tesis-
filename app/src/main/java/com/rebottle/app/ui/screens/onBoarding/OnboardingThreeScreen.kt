package com.rebottle.app.ui.screens.onBoarding

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.rebottle.app.R
import com.rebottle.app.components.OnboardingScreen
import com.rebottle.app.navigation.AppScreens

@Composable
fun OnboardingThreeScreen(controller: NavController) {

    val context = LocalContext.current

    OnboardingScreen(
        imageRes = R.drawable.onboarding3,
        title = "Recicla y obtén\nrecompensas",
        description = "Cada recolección registrada suma puntos que podrás canjear " +
                "por beneficios, mientras ayudas a construir un entorno más " +
                "limpio y sostenible.",
        selectedPage = 2,
        buttonText = "Empezar",
        onButtonClick = {
            OnboardingPrefs.setOnboardingCompleted(context)
            controller.navigate(AppScreens.register.name) {
                popUpTo(AppScreens.onboardingOne.name) { inclusive = true }
            }
        }
    )
}