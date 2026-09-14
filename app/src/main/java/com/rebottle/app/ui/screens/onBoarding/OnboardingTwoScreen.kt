package com.rebottle.app.ui.screens.onBoarding

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.rebottle.app.R
import com.rebottle.app.components.OnboardingScreen
import com.rebottle.app.navigation.AppScreens

@Composable
fun OnboardingTwoScreen(controller: NavController) {

    OnboardingScreen(
        imageRes = R.drawable.onboarding2,
        title = "Agenda una recolección\nfácilmente",
        description = "Programa una visita con un recolector, indica qué materiales " +
                "deseas entregar y conoce el estado de tu solicitud en tiempo real.",
        selectedPage = 1,
        buttonText = "Siguiente",
        onButtonClick = {
            controller.navigate(AppScreens.onboardingThree.name)
        }
    )
}