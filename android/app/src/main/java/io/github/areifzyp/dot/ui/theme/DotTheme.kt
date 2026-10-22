package io.github.areifzyp.dot.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import io.github.areifzyp.dot.R

val DotoFont = FontFamily(
    Font(R.font.doto, FontWeight.Thin, androidx.compose.ui.text.font.FontStyle.Normal),
    Font(R.font.doto, FontWeight.Light, androidx.compose.ui.text.font.FontStyle.Normal),
    Font(R.font.doto, FontWeight.Normal, androidx.compose.ui.text.font.FontStyle.Normal),
    Font(R.font.doto, FontWeight.Medium, androidx.compose.ui.text.font.FontStyle.Normal),
    Font(R.font.doto, FontWeight.SemiBold, androidx.compose.ui.text.font.FontStyle.Normal),
    Font(R.font.doto, FontWeight.Bold, androidx.compose.ui.text.font.FontStyle.Normal),
    Font(R.font.doto, FontWeight.ExtraBold, androidx.compose.ui.text.font.FontStyle.Normal),
    Font(R.font.doto, FontWeight.Black, androidx.compose.ui.text.font.FontStyle.Normal),
)

private val LightColorScheme = lightColorScheme(
    primary = DotLightForeground,
    onPrimary = DotLightBackground,
    secondary = DotLightSecondary,
    background = DotLightBackground,
    onBackground = DotLightForeground,
    surface = DotLightSurface,
    onSurface = DotLightForeground,
    error = DotAccentRed,
)

private val DarkColorScheme = darkColorScheme(
    primary = DotDarkForeground,
    onPrimary = DotDarkBackground,
    secondary = DotDarkSecondary,
    background = DotDarkBackground,
    onBackground = DotDarkForeground,
    surface = DotDarkSurface,
    onSurface = DotDarkForeground,
    error = DotAccentRed,
)

@Composable
fun DotTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = DotTypography,
        content = content
    )
}