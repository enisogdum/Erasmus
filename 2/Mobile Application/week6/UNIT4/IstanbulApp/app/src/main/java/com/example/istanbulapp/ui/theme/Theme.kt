package com.example.istanbulapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────────
//  🌗 Istanbul App Theme
//
//  MaterialTheme is the design system for our app.
//  It provides colors, typography and shapes that all
//  composables can access via MaterialTheme.colorScheme
//
//  We define two color schemes:
//    - LightColorScheme (default - used during the day)
//    - DarkColorScheme  (used when system is in dark mode)
// ─────────────────────────────────────────────

private val LightColorScheme = lightColorScheme(
    primary          = IstanbulRed,       // Main brand color (buttons, highlights)
    onPrimary        = TextOnDark,         // Text on top of primary color
    primaryContainer = IstanbulRedLight,   // Lighter version for containers
    secondary        = BosphorusBlue,      // Accent color
    onSecondary      = TextOnDark,
    tertiary         = BazaarGold,         // Third accent color
    onTertiary       = TextOnDark,
    background       = WarmWhite,          // App background
    onBackground     = TextPrimary,        // Text on background
    surface          = WarmSurface,        // Card/surface backgrounds
    onSurface        = TextPrimary,        // Text on surfaces
    surfaceVariant   = CardWhite,
    onSurfaceVariant = TextSecondary,
    outline          = TextSecondary
)

private val DarkColorScheme = darkColorScheme(
    primary          = IstanbulRedLight,
    onPrimary        = TextOnDark,
    primaryContainer = IstanbulRedDark,
    secondary        = BosphorusBlueLight,
    onSecondary      = TextOnDark,
    tertiary         = BazaarGoldLight,
    background       = DarkBackground,
    onBackground     = WarmWhite,
    surface          = DarkSurface,
    onSurface        = WarmWhite,
    surfaceVariant   = Color(0xFF3D2A1A),
    onSurfaceVariant = WarmWhite.copy(alpha = 0.7f)
)

@Composable
fun IstanbulAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // 💡 We set dynamicColor = false so our Istanbul colors always show,
    //    instead of being overridden by the user's system wallpaper colors.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography  = Typography,   // Defined in Type.kt
        content     = content
    )
}