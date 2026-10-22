package io.github.areifzyp.dot.style

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.updateAll
import io.github.areifzyp.dot.decorative.DecorativeWidget
import io.github.areifzyp.dot.music.MusicWidget
import io.github.areifzyp.dot.system.DeviceStatusWidget
import io.github.areifzyp.dot.weather.WeatherWidget
import io.github.areifzyp.dot.widget.CalendarWidget
import io.github.areifzyp.dot.widget.ClockWidget
import io.github.areifzyp.dot.widget.TextWidgetProvider

/**
 * GAY-4: setiap perubahan gaya memicu update SELURUH widget terpasang.
 *
 * Dipanggil dari layar Gaya setelah preferensi disimpan. Mekanisme:
 * [androidx.glance.appwidget.updateAll] per kelas widget — Glance menangani
 * enumerasi GlanceId internal.
 */
object StyleRefresher {
    /** Semua kelas widget Dot (PRD §6.8 GAY-4). */
    val allWidgets: List<Class<out GlanceAppWidget>> = listOf(
        ClockWidget::class.java,
        CalendarWidget::class.java,
        WeatherWidget::class.java,
        DeviceStatusWidget::class.java,
        MusicWidget::class.java,
        DecorativeWidget::class.java,
    )

    suspend fun refreshAll(context: Context) {
        // Widget Teks memakai RemoteViews biasa, bukan Glance — update terpisah.
        runCatching { TextWidgetProvider.updateAll(context) }
        allWidgets.forEach { cls ->
            try {
                cls.getDeclaredConstructor().newInstance().updateAll(context)
            } catch (_: Exception) {
                // widget tipe tertentu gagal; tipe lain tetap di-update
            }
        }
    }
}