package io.github.areifzyp.dot.widget

import android.content.Context
import android.content.res.Configuration
import androidx.compose.ui.graphics.Color
import androidx.glance.unit.ColorProvider

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