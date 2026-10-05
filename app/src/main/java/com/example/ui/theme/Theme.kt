package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppThemeColors(
    val isDark: Boolean,
    val bg: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val cyan: Color,
    val cyanSoft: Color,
    val green: Color,
    val greenSoft: Color,
    val red: Color,
    val redSoft: Color,
    val amber: Color,
    val amberSoft: Color
)

val DarkAppThemeColors = AppThemeColors(
    isDark = true,
    bg = MarketDarkBg,
    surface = MarketSurface,
    surfaceVariant = MarketSurfaceVariant,
    border = MarketCardBorder,
    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    textTertiary = TextTertiary,
    cyan = TechCyan,
    cyanSoft = TechCyanSoft,
    green = BullGreen,
    greenSoft = BullGreenSoft,
    red = BearRed,
    redSoft = BearRedSoft,
    amber = GoldAmber,
    amberSoft = GoldAmberSoft
)

val LightAppThemeColors = AppThemeColors(
    isDark = false,
    bg = MarketLightBg,
    surface = MarketLightSurface,
    surfaceVariant = MarketLightSurfaceVariant,
    border = MarketLightCardBorder,
    textPrimary = TextPrimaryLight,
    textSecondary = TextSecondaryLight,
    textTertiary = TextTertiaryLight,
    cyan = TechCyanLight,
    cyanSoft = TechCyanSoftLight,
    green = BullGreenLight,
    greenSoft = BullGreenSoftLight,
    red = BearRedLight,
    redSoft = BearRedSoftLight,
    amber = GoldAmberLight,
    amberSoft = GoldAmberSoftLight
)

val LocalAppColors = staticCompositionLocalOf { DarkAppThemeColors }

object AppTheme {
    val colors: AppThemeColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current
}

private val DarkColorScheme = darkColorScheme(
    primary = TechCyan,
    onPrimary = Color.Black,
    primaryContainer = TechCyanSoft,
    onPrimaryContainer = TechCyan,
    secondary = BullGreen,
    onSecondary = Color.Black,
    secondaryContainer = BullGreenSoft,
    onSecondaryContainer = BullGreen,
    tertiary = GoldAmber,
    onTertiary = Color.Black,
    tertiaryContainer = GoldAmberSoft,
    onTertiaryContainer = GoldAmber,
    background = MarketDarkBg,
    onBackground = TextPrimary,
    surface = MarketSurface,
    onSurface = TextPrimary,
    surfaceVariant = MarketSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = MarketCardBorder,
    error = BearRed,
    onError = Color.White,
    errorContainer = BearRedSoft,
    onErrorContainer = BearRed
)

private val LightColorScheme = lightColorScheme(
    primary = TechCyanLight,
    onPrimary = Color.White,
    primaryContainer = TechCyanSoftLight,
    onPrimaryContainer = TechCyanLight,
    secondary = BullGreenLight,
    onSecondary = Color.White,
    secondaryContainer = BullGreenSoftLight,
    onSecondaryContainer = BullGreenLight,
    tertiary = GoldAmberLight,
    onTertiary = Color.White,
    tertiaryContainer = GoldAmberSoftLight,
    onTertiaryContainer = GoldAmberLight,
    background = MarketLightBg,
    onBackground = TextPrimaryLight,
    surface = MarketLightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = MarketLightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = MarketLightCardBorder,
    error = BearRedLight,
    onError = Color.White,
    errorContainer = BearRedSoftLight,
    onErrorContainer = BearRedLight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val appThemeColors = if (darkTheme) DarkAppThemeColors else LightAppThemeColors

    CompositionLocalProvider(LocalAppColors provides appThemeColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
