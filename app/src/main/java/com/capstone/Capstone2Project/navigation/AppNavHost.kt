package com.capstone.Capstone2Project.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.capstone.Capstone2Project.ui.screen.auth.AuthViewModel
import com.capstone.Capstone2Project.ui.screen.auth.LoginScreen
import com.capstone.Capstone2Project.ui.screen.auth.SignUpScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_LOGIN
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable(ROUTE_LOGIN) {
            LoginScreen(navController)
        }

        composable(ROUTE_SIGNUP) {
            SignUpScreen(navController)
        }

        composable(ROUTE_HOME) {

        }
    }
}