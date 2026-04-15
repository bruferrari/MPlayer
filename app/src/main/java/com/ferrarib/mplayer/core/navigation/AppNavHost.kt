package com.ferrarib.mplayer.core.navigation

import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ferrarib.mplayer.core.ui.DevicePosture
import com.ferrarib.mplayer.features.album.AlbumScreen
import com.ferrarib.mplayer.features.player.PlayerScreen
import com.ferrarib.mplayer.features.songs.SongsScreen

@Composable
fun AppNavHost(
    windowSizeClass: WindowSizeClass,
    devicePosture: DevicePosture = DevicePosture.Normal,
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppDestinations.SONGS
    ) {
        composable(AppDestinations.SONGS) {
            SongsScreen(
                windowSizeClass = windowSizeClass,
                onSongClick = { song ->
                    navController.navigate(AppDestinations.player(song.trackId))
                },
                onViewAlbum = { song ->
                    navController.navigate(AppDestinations.album(song.collectionId))
                }
            )
        }
        composable(
            route = AppDestinations.PLAYER_ROUTE,
            arguments = listOf(navArgument(AppDestinations.ARG_TRACK_ID) { type = NavType.LongType })
        ) {
            PlayerScreen(
                windowSizeClass = windowSizeClass,
                devicePosture = devicePosture,
                onBack = { navController.popBackStack() },
                onViewAlbum = { collectionId ->
                    navController.navigate(AppDestinations.album(collectionId)) {
                        popUpTo(AppDestinations.PLAYER_ROUTE) { inclusive = true }
                    }
                },
            )
        }
        composable(
            route = AppDestinations.ALBUM_ROUTE,
            arguments = listOf(navArgument(AppDestinations.ARG_COLLECTION_ID) { type = NavType.LongType })
        ) {
            AlbumScreen(
                windowSizeClass = windowSizeClass,
                onBack = { navController.popBackStack() },
                onSongClick = { song ->
                    navController.navigate(AppDestinations.player(song.trackId))
                },
            )
        }
    }
}
