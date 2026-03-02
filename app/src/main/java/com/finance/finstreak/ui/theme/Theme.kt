package com.finance.finstreak.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private val LightColorScheme = lightColorScheme(
    primary = LightColorTokens.primary,
    onPrimary = LightColorTokens.onPrimary,
    primaryContainer = LightColorTokens.primaryLight,
    onPrimaryContainer = LightColorTokens.primaryDark,
    secondary = LightColorTokens.secondary,
    onSecondary = LightColorTokens.onSecondary,
    secondaryContainer = LightColorTokens.secondary.copy(alpha = 0.25f),
    onSecondaryContainer = LightColorTokens.secondaryDark,
    tertiary = LightColorTokens.warning,
    onTertiary = LightColorTokens.onPrimary,
    background = LightColorTokens.background,
    onBackground = LightColorTokens.onBackground,
    surface = LightColorTokens.surface,
    onSurface = LightColorTokens.onSurface,
    surfaceVariant = LightColorTokens.surfaceVariant,
    onSurfaceVariant = LightColorTokens.textSecondary,
    outline = LightColorTokens.border,
    error = LightColorTokens.error,
    onError = LightColorTokens.onError
)

@Composable
fun FinStreakTheme(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalColorTokens provides LightColorTokens,
        LocalTypographyTokens provides DefaultTypographyTokens,
        LocalShapeTokens provides DefaultShapeTokens,
        LocalDimensionTokens provides DefaultDimensionTokens
    ) {
        MaterialTheme(
            colorScheme = LightColorScheme,
            typography = FinStreakTypography,
            shapes = FinStreakShapes,
            content = content
        )
    }
}
