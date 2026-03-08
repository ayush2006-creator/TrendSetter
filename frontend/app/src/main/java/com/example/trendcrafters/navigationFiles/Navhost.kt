package com.example.trendcrafters.navigationFiles

import androidx.compose.runtime.Composable
import androidx.navigation.NavType // <-- ADD THIS IMPORT
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument // <-- ADD THIS IMPORT

import com.example.trendcrafters.SplashScreen
import com.example.trendcrafters.Auth.AuthScreenHandler
import com.example.trendcrafters.Home.Home
import com.example.trendcrafters.onboarding.QuestionnaireHandler
import com.example.trendcrafters.onboarding.TodayTrend

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screens.SplashScreen.route) {
        composable(Screens.SplashScreen.route) {
            SplashScreen({ navController.navigate(Screens.TodayTrend.route) })
        }
        composable(Screens.TodayTrend.route) {
            TodayTrend({ navController.navigate(Screens.Auth.route) })
        }
        composable(Screens.Auth.route){
            AuthScreenHandler(navController)
        }

        // --- UPDATED ROUTE ---
        // Accept the parameter as a query argument so spaces don't crash the app
        composable(
            route = "${Screens.Onboarding.route}?displayName={displayName}",
            arguments = listOf(navArgument("displayName") {
                type = NavType.StringType
                defaultValue = "Creator"
            })
        ) { backStackEntry ->
            // Extract the passed string
            val passedName = backStackEntry.arguments?.getString("displayName") ?: "Creator"

            QuestionnaireHandler(
                displayName = passedName,
                onProfileSaved = {
                    // Navigate to Home screen when onboarding finishes
                    navController.navigate(Screens.Home.route) {
                        popUpTo("${Screens.Onboarding.route}?displayName={displayName}") { inclusive = true }
                    }
                }
            )

        }
        composable(Screens.Home.route) {
            Home()
        }


    }
}