package com.example.curiosity.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CuriosityColorPalette = darkColorScheme(
    primary = ElectricBlue,
    secondary = VividMagenta,
    tertiary = GoldenYellow,
    background = BlackBackgroundShade,
    outline = Color.DarkGray
)

@Composable
fun CuriosityTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CuriosityColorPalette,
        typography = Typography,
        content = content
    )
}