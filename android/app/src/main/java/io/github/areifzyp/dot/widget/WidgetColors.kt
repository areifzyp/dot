package io.github.areifzyp.dot.widget

import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.graphics.Color
import androidx.glance.unit.ColorProvider
import io.github.areifzyp.dot.style.DotStyleSettings
import io.github.areifzyp.dot.ui.theme.DotAccentOrange
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
    override fun getColor(context: Context): Color {
        val nightMode = context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK
        return if (nightMode == Configuration.UI_MODE_NIGHT_YES) night else day
    }
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

/** ColorProvider aksen yang menghormati pilihan Gaya Nothing (GAY-3/GAY-4). */
internal class AccentColorProvider : ColorProvider {
    override fun getColor(context: Context): Color = accentColorFor(context)
}