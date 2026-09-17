package com.rebottle.app.ui.screens.onBoarding



import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.rebottle.app.R
import com.rebottle.app.components.OnboardingScreen
import com.rebottle.app.navigation.AppScreens

@Composable
fun OnboardingOneScreen(controller: NavController) {

    OnboardingScreen(
        imageRes = R.drawable.onbording1,
        title = "Bienvenido a\nReBottle",
        description = "ReBottle facilita la gestión de tus residuos reciclables. " +
                "Solicita una recolección, organiza tus materiales y contribuye " +
                "al cuidado del ambiente desde un solo lugar.",
        selectedPage = 0,
        buttonText = "Siguiente",
        onButtonClick = {
            controller.navigate(AppScreens.onboardingTwo.name)
        }
    )
}