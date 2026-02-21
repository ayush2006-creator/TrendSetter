package com.example.trendcrafters.navigationFiles
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.trendcrafters.SplashScreen
import com.example.trendcrafters.Auth.AuthScreenHandler
import com.example.trendcrafters.Home.ChatScreen
import com.example.trendcrafters.Home.DraftScreen
import com.example.trendcrafters.Home.Home
import com.example.trendcrafters.Home.ProfileScreen
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
        composable (Screens.Auth.route){
            AuthScreenHandler(navController)
        }
        composable  (Screens.Onboarding.route){
            QuestionnaireHandler()
        }

    }
}