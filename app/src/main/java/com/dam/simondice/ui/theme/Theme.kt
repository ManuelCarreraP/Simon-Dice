package com.dam.simondice.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable


// Usar darkColorScheme
private val DarkColorPalette = darkColorScheme(
    primary = Purple200,

    secondary = Teal200
)

// Usar lightColorScheme
private val LightColorPalette = lightColorScheme(
    primary = Purple500,
    secondary = Teal200
)

@Composable
fun SimonDiceTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colorScheme = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}