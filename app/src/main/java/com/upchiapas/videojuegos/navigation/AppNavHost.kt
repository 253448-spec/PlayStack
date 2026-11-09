package com.upchiapas.videojuegos.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.upchiapas.videojuegos.feature_auth.presentation.login.LoginScreen
import com.upchiapas.videojuegos.feature_auth.presentation.login.LoginViewModel
import com.upchiapas.videojuegos.feature_auth.presentation.register.RegisterScreen
import com.upchiapas.videojuegos.feature_auth.presentation.register.RegisterViewModel
import com.upchiapas.videojuegos.feature_videojuegos.presentation.home.VideojuegosScreen
import com.upchiapas.videojuegos.feature_videojuegos.presentation.home.VideojuegosViewModel

@Composable
fun AppNavHost(
    navController: NavHostController,
    isLoggedIn: Boolean,
    modifier: Modifier = Modifier
) {
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = viewModel(factory = LoginViewModel.Factory)
            LoginScreen(
                viewModel = loginViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            val registerViewModel: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory)
            RegisterScreen(
                viewModel = registerViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            val videojuegosViewModel: VideojuegosViewModel = viewModel(factory = VideojuegosViewModel.Factory)
            VideojuegosScreen(
                viewModel = videojuegosViewModel,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
