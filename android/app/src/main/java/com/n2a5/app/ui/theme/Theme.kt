package com.n2a5.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val BgPrimary = Color(0xFF0D0E12)
val BgSecondary = Color(0xFF13141A)
val PinkPrimary = Color(0xFFE040FB)
val PurplePrimary = Color(0xFF7C4DFF)
val PinkDark = Color(0xFFAD1457)
val CardBg = Color(0xFF1A1B23)
val White70 = Color(0xB3FFFFFF)
val White40 = Color(0x66FFFFFF)
val White10 = Color(0x1AFFFFFF)
val GreenStatus = Color(0xFF4CAF50)
val RedStatus = Color(0xFFF44336)

private val DarkColorScheme = darkColorScheme(
    primary = PinkPrimary,
    secondary = PurplePrimary,
    tertiary = PinkDark,
    background = BgPrimary,
    surface = BgSecondary,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = CardBg,
    outline = White10,
)

@Composable
fun N2a5Theme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
