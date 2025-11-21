package com.example.eventoscomunitarios.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.example.eventoscomunitarios.ui.screens.auth.LoginScreen
import com.example.eventoscomunitarios.ui.screens.auth.RegisterScreen
import com.example.eventoscomunitarios.ui.screens.home.HomeScreen
import com.example.eventoscomunitarios.ui.screens.events.CreateEventScreen
import com.example.eventoscomunitarios.ui.screens.events.EventDetailScreen
import com.example.eventoscomunitarios.ui.screens.events.HistoryScreen
import com.example.eventoscomunitarios.viewmodel.AuthViewModel
import com.example.eventoscomunitarios.viewmodel.EventViewModel

sealed class AppRoute(val route: String) {
    object Login : AppRoute("login")
    object Register : AppRoute("register")
    object Home : AppRoute("home")
    object CreateEvent : AppRoute("createEvent")
    object History : AppRoute("history")
    object EventDetail : AppRoute("eventDetail/{eventId}") {
        fun create(eventId: String) = "eventDetail/$eventId"
    }
}

@Composable
fun AppNav(navController: NavHostController) {

    val authViewModel: AuthViewModel = viewModel()
    val eventViewModel: EventViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = AppRoute.Login.route
    ) {
        composable(AppRoute.Login.route) {
            LoginScreen(navController, authViewModel)
        }
        composable(AppRoute.Register.route) {
            RegisterScreen(navController, authViewModel)
        }
        composable(AppRoute.Home.route) {
            HomeScreen(
                navController = navController,
                eventViewModel = eventViewModel,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(AppRoute.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoute.CreateEvent.route) {
            CreateEventScreen(navController, eventViewModel)
        }
        composable(AppRoute.History.route) {
            HistoryScreen(navController, eventViewModel)
        }
        composable(
            route = AppRoute.EventDetail.route,
            arguments = listOf(navArgument("eventId") { type = NavType.StringType })
        ) { backStackEntry ->
            val eventId = backStackEntry.arguments?.getString("eventId") ?: ""
            EventDetailScreen(navController, eventViewModel, eventId)
        }
    }
}
