package com.ferrarib.mplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.exoplayer.ExoPlayer
import com.ferrarib.mplayer.core.navigation.AppNavHost
import com.ferrarib.mplayer.core.ui.DevicePosture
import com.ferrarib.mplayer.core.ui.devicePostureFlow
import com.ferrarib.mplayer.ui.theme.MPlayerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var exoPlayer: ExoPlayer

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            val postureFlow = remember { devicePostureFlow(this) }
            val devicePosture by postureFlow.collectAsStateWithLifecycle(initialValue = DevicePosture.Normal)
            MPlayerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(
                        windowSizeClass = windowSizeClass,
                        devicePosture = devicePosture,
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isFinishing) {
            exoPlayer.stop()
            exoPlayer.release()
        }
    }
}
