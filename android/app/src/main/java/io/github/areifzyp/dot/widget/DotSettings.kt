package io.github.areifzyp.dot.widget

import android.content.Context
import android.text.format.DateFormat

/**
 * Preferensi widget per appwidget ID (PRD §5 JAM-2: Format Waktu).
 * Disimpan di SharedPreferences terpisah; default = SYSTEM (mengikuti pengaturan perangkat).
 */
enum class TimeFormat { SYSTEM, H12, H24 }

object DotSettings {

    private const val PREFS = "dot_widget_prefs"

    private fun key(widgetId: Int) = "time_format_$widgetId"

    fun getTimeFormat(context: Context, widgetId: Int): TimeFormat {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(key(widgetId), TimeFormat.SYSTEM.name)
        return runCatching { TimeFormat.valueOf(raw ?: TimeFormat.SYSTEM.name) }
            .getOrDefault(TimeFormat.SYSTEM)
    }

    fun setTimeFormat(context: Context, widgetId: Int, format: TimeFormat) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(key(widgetId), format.name)
            .apply()
    }

    /** Bersihkan preferensi saat widget dihapus dari homescreen. */
    fun onWidgetDeleted(context: Context, widgetId: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .remove(key(widgetId))
            .apply()
    }

    /** Resolusi ke flag 24-jam konkret; SYSTEM mengikuti pengaturan perangkat. */
    fun is24Hour(context: Context, format: TimeFormat): Boolean = when (format) {
        TimeFormat.H24 -> true
        TimeFormat.H12 -> false
        TimeFormat.SYSTEM -> DateFormat.is24HourFormat(context)
    }
}