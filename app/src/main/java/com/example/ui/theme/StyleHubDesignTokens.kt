package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Enterprise Design Tokens for StyleHub South Africa.
 * Preserves the exact luxury Amber Gold and Charcoal brand palette while
 * providing consistent spacing, typography scales, elevation, and semantic tokens.
 */
object StyleHubDesignTokens {

    // Brand Colors (Preserved from existing identity)
    val Primary = GoldPrimary
    val Secondary = GoldSecondary
    val Accent = GoldTertiary
    val AccentLight = GoldLight
    val PrimaryContainer = GoldContainer
    val OnPrimaryContainer = OnGoldContainer

    // Dark Surface & Backgrounds
    val DarkBackground = CharcoalDark
    val DarkSurface = CharcoalSurface
    val DarkSurfaceVariant = CharcoalSurfaceVariant
    val DarkBorder = CharcoalBorder
    val DarkTextSecondary = CharcoalTextSecondary

    // Light Surface & Backgrounds
    val LightBackground = SurfaceLight
    val LightCard = CardLight
    val LightTextDark = TextDark
    val LightTextMuted = TextMuted
    val LightBorder = BorderLight

    // Semantic Status Colors (Accessible & Harmonious with Gold / Charcoal)
    val StatusSuccess = EmeraldVerified       // #10B981
    val StatusSuccessContainer = EmeraldContainer // #064E3B
    val StatusWarning = StarGold              // #F59E0B
    val StatusWarningContainer = Color(0xFF452203)
    val StatusError = CrimsonCancel           // #EF4444
    val StatusErrorContainer = Color(0xFF4C0F0F)
    val StatusInfo = Color(0xFF3B82F6)        // Accessible enterprise blue
    val StatusInfoContainer = Color(0xFF1E3A8A)

    // Interactive States
    val DisabledAlpha = 0.38f
    val HoverAlpha = 0.08f
    val PressedAlpha = 0.12f
    val SubtleBorderAlpha = 0.4f

    // Spacing Grid (8dp standard)
    val Space2 = 2.dp
    val Space4 = 4.dp
    val Space8 = 8.dp
    val Space12 = 12.dp
    val Space16 = 16.dp
    val Space20 = 20.dp
    val Space24 = 24.dp
    val Space32 = 32.dp
    val Space48 = 48.dp

    // Corner Radius
    val RadiusSmall = RoundedCornerShape(6.dp)
    val RadiusMedium = RoundedCornerShape(10.dp)
    val RadiusCard = RoundedCornerShape(14.dp)
    val RadiusLarge = RoundedCornerShape(18.dp)
    val RadiusFull = RoundedCornerShape(50)

    // Elevation & Depth
    val ElevationLow = 1.dp
    val ElevationCard = 2.dp
    val ElevationModal = 8.dp

    // Touch Targets (Android Accessibility Standard >= 48dp)
    val MinTouchTarget = 48.dp
}
