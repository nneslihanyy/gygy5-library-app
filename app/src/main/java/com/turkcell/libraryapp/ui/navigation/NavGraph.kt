package com.turkcell.libraryapp.ui.navigation


import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.turkcell.libraryapp.ui.screen.LoginScreen
import com.turkcell.libraryapp.ui.screen.RegisterScreen
import com.turkcell.libraryapp.ui.viewmodel.AuthViewModel

@Composable
fun NavGraph(navController: NavHostController = rememberNavController()) {

    val authViewModel: AuthViewModel = viewModel()


    NavHost(navController = navController, startDestination = Screen.Register.route)
    {
        composable(Screen.Login.route) { LoginScreen(  onNavigateToRegister={navController.navigate(Screen.Register.route)},authViewModel, onLoginSuccess = {role -> {}}) }
        composable(Screen.Register.route) { RegisterScreen(onNavigateToLogin = {navController.navigate(Screen.Login.route)},authViewModel) }
    }
}