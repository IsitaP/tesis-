package com.rebottle.app.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rebottle.app.ui.screens.HomeUserScreen
import com.rebottle.app.ui.screens.auth.LoginScreen
import com.rebottle.app.ui.screens.auth.RegisterScreen


enum class AppScreens{
    login,
    register,
    home
}

@Composable
fun Navigation(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = AppScreens.login.name){
        composable(route = AppScreens.login.name){
            LoginScreen(navController)
        }
        composable(route = AppScreens.register.name){
            RegisterScreen(navController)
        }
        composable(route = AppScreens.home.name){
            HomeUserScreen()
        }
    }
}