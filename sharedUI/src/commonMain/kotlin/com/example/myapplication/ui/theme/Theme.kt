package com.example.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * KoAkademy's palette in Material 3 roles: warm paper surfaces, near-black ink, and a
 * terracotta accent. Every surface container is defined so Material components (dialogs,
 * sheets, navigation bars, switches) stay on-paper instead of falling back to the default
 * purple-grey scheme.
 */
private val LightColors = lightColorScheme(
    primary = Color(0xFF2C2924),
    onPrimary = Color(0xFFF8F3E7),
    primaryContainer = Color(0xFFE5DECC),
    onPrimaryContainer = Color(0xFF2C2924),
    inversePrimary = Color(0xFFD6CDB9),
    secondary = Color(0xFFA54A22),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFF3D9C8),
    onSecondaryContainer = Color(0xFF47200C),
    tertiary = Color(0xFF4E5C3B),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFDDE5CD),
    onTertiaryContainer = Color(0xFF222B16),
    background = Color(0xFFEDE7DA),
    onBackground = Color(0xFF292620),
    surface = Color(0xFFF8F4E9),
    onSurface = Color(0xFF292620),
    surfaceVariant = Color(0xFFE6DFCE),
    onSurfaceVariant = Color(0xFF645D4F),
    surfaceTint = Color(0xFF2C2924),
    inverseSurface = Color(0xFF35322C),
    inverseOnSurface = Color(0xFFF3EDDE),
    error = Color(0xFFA3392C),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFF7DAD4),
    onErrorContainer = Color(0xFF3E0C06),
    outline = Color(0xFFA69D8A),
    outlineVariant = Color(0xFFD8D0BC),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFFFFFBF1),
    surfaceDim = Color(0xFFE2DBCB),
    surfaceContainerLowest = Color(0xFFFFFCF3),
    surfaceContainerLow = Color(0xFFF4EFE2),
    surfaceContainer = Color(0xFFEFE9DA),
    surfaceContainerHigh = Color(0xFFE9E2D1),
    surfaceContainerHighest = Color(0xFFE2DBC8),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFFEAE3D1),
    onPrimary = Color(0xFF2E2B25),
    primaryContainer = Color(0xFF3E3A31),
    onPrimaryContainer = Color(0xFFF3ECD9),
    inversePrimary = Color(0xFF5C5546),
    secondary = Color(0xFFE29367),
    onSecondary = Color(0xFF3A1A0A),
    secondaryContainer = Color(0xFF53301D),
    onSecondaryContainer = Color(0xFFF9DCC9),
    tertiary = Color(0xFFB9C6A4),
    onTertiary = Color(0xFF25301A),
    tertiaryContainer = Color(0xFF333D26),
    onTertiaryContainer = Color(0xFFDAE6C5),
    background = Color(0xFF171612),
    onBackground = Color(0xFFEDE6D7),
    surface = Color(0xFF201F1A),
    onSurface = Color(0xFFEDE6D7),
    surfaceVariant = Color(0xFF49453B),
    onSurfaceVariant = Color(0xFFBDB4A1),
    surfaceTint = Color(0xFFEAE3D1),
    inverseSurface = Color(0xFFEDE6D7),
    inverseOnSurface = Color(0xFF2B2822),
    error = Color(0xFFFFB4A8),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF8C1D18),
    onErrorContainer = Color(0xFFFFDAD4),
    outline = Color(0xFF857C6B),
    outlineVariant = Color(0xFF3B372F),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFF35322B),
    surfaceDim = Color(0xFF171612),
    surfaceContainerLowest = Color(0xFF12110E),
    surfaceContainerLow = Color(0xFF1B1A16),
    surfaceContainer = Color(0xFF211F1A),
    surfaceContainerHigh = Color(0xFF2A2822),
    surfaceContainerHighest = Color(0xFF332F28),
)

/** Serif display type gives the portal its editorial, academic character. */
private val AppTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 34.sp,
        lineHeight = 40.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.6).sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 30.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 23.sp,
        lineHeight = 29.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.3).sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Serif,
        fontSize = 19.sp,
        lineHeight = 25.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = (-0.1).sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 15.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 16.sp,
        lineHeight = 25.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.1.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        lineHeight = 21.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.1.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 12.sp,
        lineHeight = 17.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.1.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 14.sp,
        lineHeight = 19.sp,
        fontWeight = FontWeight.SemiBold,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.3.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontSize = 10.5.sp,
        lineHeight = 14.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.4.sp,
    ),
)

private val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(14.dp),
    large = RoundedCornerShape(18.dp),
    extraLarge = RoundedCornerShape(24.dp),
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        shapes = AppShapes,
        content = content,
    )
}
