package io.github.areifzyp.dot.widget

import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.Font
import androidx.glance.unit.ColorProvider
import io.github.areifzyp.dot.R
import io.github.areifzyp.dot.style.DotStyleSettings
import io.github.areifzyp.dot.ui.theme.DotAccentOrange
import io.github.areifzyp.dot.ui.theme.DotDarkBackground
import io.github.areifzyp.dot.ui.theme.DotDarkForeground
import io.github.areifzyp.dot.ui.theme.DotLightBackground
import io.github.areifzyp.dot.ui.theme.DotLightForeground
import io.github.areifzyp.dot.ui.theme.DotAccentRed

/**
 * Glance 1.1.0 tidak punya factory publik ColorProvider(day, night) —
 * hanya ColorProvider(color) dan ColorProvider(resId). Helper ini
 * mengimplementasikan interface ColorProvider dan memilih warna
 * day/night dari uiMode context widget saat render.
 *
 * Dipakai bersama oleh seluruh widget Dot (Jam, Kalender, Cuaca).
 */
internal class DayNightColor(
    private val day: Color,
    private val night: Color,
) : ColorProvider {
    override fun getColor(context: Context): Color = if (isWidgetDark(context)) night else day
}

/**
 * PRD §17.1: widget wajib mengikuti pilihan tema aplikasi (Ikuti Sistem, Terang,
 * Gelap), bukan hanya uiMode sistem. Dipakai semua pasangan DayNightColor.
 */
internal fun isWidgetDark(context: Context): Boolean =
    when (DotStyleSettings.getTheme(context)) {
        DotStyleSettings.ThemeMode.SYSTEM ->
            (context.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        DotStyleSettings.ThemeMode.LIGHT -> false
        DotStyleSettings.ThemeMode.DARK -> true
    }

/**
 * GAY-3 di sisi widget: warna aksen terpilih (merah Nothing default, oranye
 * preset, atau Warna Sistem API 31+). Konteks widget (RemoteViews) tidak
 * mendukung DynamicColors penuh, jadi Material You dibaca dari atribut tema
 * sistem; kegagalan → fallback merah (aturan §8.5: default selalu palet dasar).
 */
internal fun accentColorFor(context: Context): Color {
    return when (DotStyleSettings.getAccent(context)) {
        DotStyleSettings.AccentChoice.NOTHING_RED -> DotAccentRed
        DotStyleSettings.AccentChoice.PRESET_ORANGE -> DotAccentOrange
        DotStyleSettings.AccentChoice.SYSTEM_MATERIAL -> systemAccentOrFallback(context)
    }
}

private fun systemAccentOrFallback(context: Context): Color = try {
    val tv = android.util.TypedValue()
    val resolved = context.theme.resolveAttribute(android.R.attr.colorAccent, tv, true)
    if (resolved && tv.type >= android.util.TypedValue.TYPE_FIRST_COLOR_INT &&
        tv.type <= android.util.TypedValue.TYPE_LAST_COLOR_INT
    ) {
        Color(tv.data)
    } else {
        DotAccentRed
    }
} catch (_: Exception) {
    DotAccentRed
}

/** Warna ARGB untuk widget RemoteViews (Widget Teks) — ikut tema + §8.5. */
internal fun widgetBackgroundArgb(context: Context): Int =
    (if (isWidgetDark(context)) DotDarkBackground else DotLightBackground).toArgb()

internal fun widgetForegroundArgb(context: Context): Int =
    (if (isWidgetDark(context)) DotDarkForeground else DotLightForeground).toArgb()

/** ColorProvider aksen yang menghormati pilihan Gaya Nothing (GAY-3/GAY-4). */
internal class AccentColorProvider : ColorProvider {
    override fun getColor(context: Context): Color = accentColorFor(context)
}

/**
 * GAY-2: helper font dinamis untuk widget Glance.
 * Mengembalikan font Doto jika diaktifkan oleh pengguna, atau null (sans-serif sistem) bila dinonaktifkan.
 */
internal fun widgetFontFamily(context: Context): FontFamily? {
    return if (DotStyleSettings.getFont(context) == DotStyleSettings.FontChoice.DOTO) {
        FontFamily(Font(R.font.doto))
    } else {
        null
    }
}
