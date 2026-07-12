package com.example.groupgo.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = TealPrimary,
    onPrimary = Color(0xFF0F172A), // Slate 900
    secondary = PurpleSecondary,
    onSecondary = Color.White,
    tertiary = AccentGold,
    background = NeutralDarkBg,
    onBackground = Color(0xFFE2E8F0), // Slate 200
    surface = NeutralDarkSurface,
    onSurface = Color(0xFFF1F5F9), // Slate 100
    surfaceVariant = NeutralDarkCard,
    onSurfaceVariant = Color(0xFFCBD5E1) // Slate 300
)

private val LightColorScheme = lightColorScheme(
    primary = TealPrimary,
    onPrimary = Color.White,
    secondary = PurpleSecondary,
    onSecondary = Color.White,
    tertiary = AccentGold,
    background = NeutralLightBg,
    onBackground = Color(0xFF1E293B), // Slate 800
    surface = NeutralLightSurface,
    onSurface = Color(0xFF0F172A), // Slate 900
    surfaceVariant = NeutralLightCard,
    onSurfaceVariant = Color(0xFF475569) // Slate 600
)

@Composable
fun GroupGoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Set dynamicColor to false by default to ensure signature Teal/Purple branding
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}