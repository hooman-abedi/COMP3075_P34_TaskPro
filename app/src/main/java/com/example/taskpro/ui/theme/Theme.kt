package com.example.taskpro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Blue = Color(0xFF1769E8)
private val Navy = Color(0xFF123A70)
private val PaleBlue = Color(0xFFEAF2FF)
private val Ink = Color(0xFF152238)

@Composable
fun TaskProTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Blue,
            onPrimary = Color.White,
            secondary = Navy,
            background = Color(0xFFF6F8FC),
            surface = Color.White,
            onSurface = Ink,
            surfaceVariant = PaleBlue
        ),
        content = content
    )
}
