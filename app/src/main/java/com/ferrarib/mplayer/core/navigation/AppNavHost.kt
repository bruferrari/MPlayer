package com.ferrarib.mplayer.core.navigation

import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ferrarib.mplayer.features.splash.SplashScreen

@Composable
fun AppNavHost(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppDestinations.SPLASH
    ) {
        composable(AppDestinations.SPLASH) {
            SplashScreen(
                onReady = {
                    navController.navigate(AppDestinations.SONGS) {
                        popUpTo(AppDestinations.SPLASH) { inclusive = true }
                    }
                }
            )
        }
        composable(AppDestinations.SONGS) {
            // Placeholder until Phase 2.
            Text(text = "Songs screen — Phase 2")
        }
        composable(AppDestinations.PLAYER_ROUTE) {
            Text(text = "Player — Phase 3")
        }
        composable(AppDestinations.ALBUM_ROUTE) {
            Text(text = "Album — Phase 4")
        }
    }
}
