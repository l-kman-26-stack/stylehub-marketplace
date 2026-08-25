package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldSecondary,
    onPrimary = CharcoalDark,
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = GoldTertiary,
    onSecondary = CharcoalDark,
    background = CharcoalDark,
    onBackground = Color(0xFFEDEDED),
    surface = CharcoalSurface,
    onSurface = Color(0xFFF3F4F6),
    surfaceVariant = CharcoalSurfaceVariant,
    onSurfaceVariant = CharcoalTextSecondary,
    outline = CharcoalBorder,
    error = CrimsonCancel
)

private val LightColorScheme = lightColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.White,
    primaryContainer = GoldLight,
    onPrimaryContainer = GoldContainer,
    secondary = GoldSecondary,
    onSecondary = Color.White,
    background = SurfaceLight,
    onBackground = TextDark,
    surface = CardLight,
    onSurface = TextDark,
    surfaceVariant = Color(0xFFF3F4F6),
    onSurfaceVariant = TextMuted,
    outline = BorderLight,
    error = CrimsonCancel
)

@Composable
fun StyleHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
