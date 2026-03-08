package com.example.trendcrafters.navigationFiles



sealed class Screens (val route :String){
    object Home:Screens("home")
    object SplashScreen:Screens ("splashscreen")
    object TodayTrend:Screens("todaytrend")
    object Auth:Screens("auth")

    object Onboarding : Screens("onboarding")




}