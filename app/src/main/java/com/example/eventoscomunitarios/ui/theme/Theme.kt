package com.example.eventoscomunitarios.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF006874),
    onPrimary = androidx.compose.ui.graphics.Color.White,
    secondary = androidx.compose.ui.graphics.Color(0xFF4A6367),
    onSecondary = androidx.compose.ui.graphics.Color.White,
)

private val DarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF4FD8EB),
    onPrimary = androidx.compose.ui.graphics.Color(0xFF00363D),
    secondary = androidx.compose.ui.graphics.Color(0xFFB1CBD0),
    onSecondary = androidx.compose.ui.graphics.Color(0xFF1C3438),
)

@Composable
fun EventosComunitariosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colorScheme = colorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
