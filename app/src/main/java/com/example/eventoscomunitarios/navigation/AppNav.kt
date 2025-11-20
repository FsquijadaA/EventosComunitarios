package com.example.eventoscomunitarios.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.eventoscomunitarios.ui.screens.auth.LoginScreen
import com.example.eventoscomunitarios.ui.screens.auth.RegisterScreen
import com.example.eventoscomunitarios.ui.screens.home.HomeScreen

sealed class AppRoute(val route: String) {
    object Login : AppRoute("login")
    object Register : AppRoute("register")
    object Home : AppRoute("home")
}

@Composable
fun AppNav(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AppRoute.Login.route
    ) {
        composable(AppRoute.Login.route) {
            LoginScreen(navController)
        }
        composable(AppRoute.Register.route) {
            RegisterScreen(navController)
        }
        composable(AppRoute.Home.route) {
            HomeScreen()
        }
    }
}
