package com.ferrarib.mplayer.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MPlayerDarkColors = darkColorScheme(
    primary = AccentBlue,
    onPrimary = BackgroundDark,
    background = BackgroundDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark
)

@Composable
fun MPlayerTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MPlayerDarkColors,
        typography = Typography,
        content = content
    )
}
