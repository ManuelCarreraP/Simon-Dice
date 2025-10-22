package com.dam.simondice.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme // <-- Usar darkColorScheme
import androidx.compose.material3.lightColorScheme // <-- Usar lightColorScheme
import androidx.compose.runtime.Composable

// Importaciones eliminadas: android.app.Activity, android.os.Build, dynamicColorScheme, LocalContext

// CORREGIDO: Usar darkColorScheme
private val DarkColorPalette = darkColorScheme(
    primary = Purple200,
    // Eliminado primaryVariant que es obsoleto en M3
    secondary = Teal200
)

// CORREGIDO: Usar lightColorScheme
private val LightColorPalette = lightColorScheme(
    primary = Purple500,
    // Eliminado primaryVariant que es obsoleto en M3
    secondary = Teal200

    /* Other default colors to override */
)

@Composable
fun SimonDiceTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    // CORREGIDO: La variable debe llamarse 'colorScheme' (o al menos usarse así en MaterialTheme)
    val colorScheme = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colorScheme, // <-- CORREGIDO: El parámetro es 'colorScheme'
        typography = Typography,
        content = content
    )
}