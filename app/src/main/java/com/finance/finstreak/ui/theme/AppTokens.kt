package com.finance.finstreak.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Color tokens ────────────────────────────────────────────────────────────

@Immutable
data class ColorTokens(
    val primary: Color,
    val primaryDark: Color,
    val primaryLight: Color,
    val secondary: Color,
    val secondaryDark: Color,
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val border: Color,
    val error: Color,
    val success: Color,
    val warning: Color,
    val onPrimary: Color,
    val onSecondary: Color,
    val onBackground: Color,
    val onSurface: Color,
    val onError: Color
)

val LightColorTokens = ColorTokens(
    primary = Color(0xFF2EBE4A),
    primaryDark = Color(0xFF1E8C35),
    primaryLight = Color(0xFFB8F0C4),
    secondary = Color(0xFFFFD600),
    secondaryDark = Color(0xFFC8A800),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFF7F7F9),
    surfaceVariant = Color(0xFFEEEEF2),
    textPrimary = Color(0xFF222222),
    textSecondary = Color(0xFF6B6B6B),
    border = Color(0xFFE0E0E0),
    error = Color(0xFFE53935),
    success = Color(0xFF43A047),
    warning = Color(0xFFFFB300),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF222222),
    onBackground = Color(0xFF222222),
    onSurface = Color(0xFF222222),
    onError = Color(0xFFFFFFFF)
)

// ─── Typography tokens ────────────────────────────────────────────────────────

@Immutable
data class TypographyTokens(
    val headline1: TextStyle,
    val headline2: TextStyle,
    val body: TextStyle,
    val bodySmall: TextStyle,
    val caption: TextStyle,
    val label: TextStyle,
    val button: TextStyle
)

val DefaultTypographyTokens = TypographyTokens(
    headline1 = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, lineHeight = 32.sp),
    headline2 = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, lineHeight = 28.sp),
    body = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Normal, lineHeight = 24.sp),
    bodySmall = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, lineHeight = 18.sp),
    caption = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal, lineHeight = 18.sp),
    label = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Medium, lineHeight = 16.sp),
    button = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, lineHeight = 24.sp)
)

// ─── Shape tokens ─────────────────────────────────────────────────────────────

@Immutable
data class ShapeTokens(
    val radiusSm: Dp,
    val radiusMd: Dp,
    val radiusLg: Dp,
    val radiusXl: Dp
)

val DefaultShapeTokens = ShapeTokens(
    radiusSm = 6.dp,
    radiusMd = 12.dp,
    radiusLg = 20.dp,
    radiusXl = 28.dp
)

// ─── Dimension tokens ─────────────────────────────────────────────────────────

@Immutable
data class DimensionTokens(
    // Spacing
    val spacingXs: Dp,
    val spacingSm: Dp,
    val spacingMd: Dp,
    val spacingLg: Dp,
    val spacingXl: Dp,
    // Elevation
    val elevationCard: Dp,
    val elevationModal: Dp,
    val elevationFab: Dp,
    // Component sizes
    val buttonHeight: Dp,
    val iconSizeSm: Dp,
    val iconSizeMd: Dp,
    val iconSizeLg: Dp,
    // Corner radii (mirrors ShapeTokens for convenient use in screens)
    val radiusSm: Dp,
    val radiusMd: Dp,
    val radiusLg: Dp,
    val radiusXl: Dp
)

val DefaultDimensionTokens = DimensionTokens(
    spacingXs = 4.dp,
    spacingSm = 8.dp,
    spacingMd = 16.dp,
    spacingLg = 24.dp,
    spacingXl = 32.dp,
    elevationCard = 2.dp,
    elevationModal = 8.dp,
    elevationFab = 6.dp,
    buttonHeight = 52.dp,
    iconSizeSm = 20.dp,
    iconSizeMd = 24.dp,
    iconSizeLg = 32.dp,
    radiusSm = 6.dp,
    radiusMd = 12.dp,
    radiusLg = 20.dp,
    radiusXl = 28.dp
)

// ─── CompositionLocals ────────────────────────────────────────────────────────

val LocalColorTokens = staticCompositionLocalOf { LightColorTokens }
val LocalTypographyTokens = staticCompositionLocalOf { DefaultTypographyTokens }
val LocalShapeTokens = staticCompositionLocalOf { DefaultShapeTokens }
val LocalDimensionTokens = staticCompositionLocalOf { DefaultDimensionTokens }

// ─── Facade ───────────────────────────────────────────────────────────────────

object LocalAppTheme {
    val colors: ColorTokens
        @androidx.compose.runtime.Composable
        get() = LocalColorTokens.current

    val typography: TypographyTokens
        @androidx.compose.runtime.Composable
        get() = LocalTypographyTokens.current

    val shapes: ShapeTokens
        @androidx.compose.runtime.Composable
        get() = LocalShapeTokens.current

    val dimens: DimensionTokens
        @androidx.compose.runtime.Composable
        get() = LocalDimensionTokens.current
}
