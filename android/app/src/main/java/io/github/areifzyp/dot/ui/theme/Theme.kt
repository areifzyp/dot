package io.github.areifzyp.dot.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import io.github.areifzyp.dot.style.DotStyleSettings

private fun lightScheme(accent: androidx.compose.ui.graphics.Color) = lightColorScheme(
    primary = DotBlack,
    onPrimary = DotWhite,
    secondary = accent,
    tertiary = accent,
    background = DotLightBackground,
    onBackground = DotLightForeground,
    surface = DotLightSurface,
    onSurface = DotLightForeground,
    onSurfaceVariant = DotLightSecondary,
)

private fun darkScheme(accent: androidx.compose.ui.graphics.Color) = darkColorScheme(
    primary = DotWhite,
    onPrimary = DotBlack,
    secondary = accent,
    tertiary = accent,
    background = DotDarkBackground,
    onBackground = DotDarkForeground,
    surface = DotDarkSurface,
    onSurface = DotDarkForeground,
    onSurfaceVariant = DotDarkSecondary,
)

/**
 * Tema aplikasi Dot — GAY-1b/2/3.
 *
 * Default = palet dasar Nothing (hitam, putih, aksen merah), BUKAN Warna Sistem
 * (aturan §8.5). Warna Sistem hanya dipakai bila pengguna memilihnya di API 31+.
 */
@Composable
fun DotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val themeMode = DotStyleSettings.getTheme(context)
    val effectiveDark = when (themeMode) {
        DotStyleSettings.ThemeMode.SYSTEM -> darkTheme
        DotStyleSettings.ThemeMode.LIGHT -> false
        DotStyleSettings.ThemeMode.DARK -> true
    }

    val accentChoice = DotStyleSettings.getAccent(context)
    val accentColor = when (accentChoice) {
        DotStyleSettings.AccentChoice.NOTHING_RED -> DotAccentRed
        DotStyleSettings.AccentChoice.PRESET_ORANGE -> DotAccentOrange
        DotStyleSettings.AccentChoice.SYSTEM_MATERIAL ->
            if (Build.VERSION.SDK_INT >= 31) {
                val dyn = if (effectiveDark) {
                    dynamicDarkColorScheme(context)
                } else {
                    dynamicLightColorScheme(context)
                }
                dyn.secondary
            } else {
                DotAccentRed
            }
    }

    val scheme = when {
        accentChoice == DotStyleSettings.AccentChoice.SYSTEM_MATERIAL && Build.VERSION.SDK_INT >= 31 ->
            if (effectiveDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        effectiveDark -> darkScheme(accentColor)
        else -> lightScheme(accentColor)
    }

    val fontChoice = DotStyleSettings.getFont(context)
    val typography = if (fontChoice == DotStyleSettings.FontChoice.SYSTEM_SANS) {
        DotSystemTypography
    } else {
        DotTypography
    }

    MaterialTheme(
        colorScheme = scheme,
        typography = typography,
        content = content,
    )
}