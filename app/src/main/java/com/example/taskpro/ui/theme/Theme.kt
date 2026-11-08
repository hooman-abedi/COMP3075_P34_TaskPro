package com.example.taskpro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Blue = Color(0xFF1769E8)
private val Navy = Color(0xFF123A70)
private val PaleBlue = Color(0xFFEAF2FF)
private val Ink = Color(0xFF152238)

val HighRed = Color(0xFFD83D4D)
val MediumOrange = Color(0xFFED9A20)
val LowGreen = Color(0xFF179B70)

private val LightColors = lightColorScheme(
    primary = Blue,
    onPrimary = Color.White,
    primaryContainer = PaleBlue,
    onPrimaryContainer = Navy,
    secondary = Navy,
    background = Color(0xFFF6F8FC),
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = PaleBlue,
    onSurfaceVariant = Color(0xFF5F6D82),
    surfaceContainer = Color.White,
    error = HighRed
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF8AB4FF),
    onPrimary = Color(0xFF002E6B),
    primaryContainer = Color(0xFF1F3B66),
    onPrimaryContainer = Color(0xFFD7E3FF),
    secondary = Color(0xFFB7C8E8),
    background = Color(0xFF0F141B),
    onBackground = Color(0xFFE2E6EE),
    surface = Color(0xFF1A212B),
    onSurface = Color(0xFFE2E6EE),
    surfaceVariant = Color(0xFF243042),
    onSurfaceVariant = Color(0xFFA9B4C6),
    surfaceContainer = Color(0xFF1A212B),
    error = Color(0xFFFF8A94)
)

@Composable
fun TaskProTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
