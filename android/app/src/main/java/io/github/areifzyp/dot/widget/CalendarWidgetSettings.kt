package io.github.areifzyp.dot.widget

import android.content.Context

/**
 * Preferensi per-widget untuk Widget Kalender (PRD §6.4 KAL-3).
 * DATE_ONLY tidak memerlukan izin; DATE_AGENDA butuh READ_CALENDAR.
 */
enum class CalendarMode { DATE_ONLY, DATE_AGENDA }

object CalendarWidgetSettings {

    private const val PREFS = "dot_calendar_widget_prefs"

    fun getMode(context: Context, widgetId: Int): CalendarMode {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString("mode_$widgetId", CalendarMode.DATE_ONLY.name)
        return runCatching { CalendarMode.valueOf(raw ?: CalendarMode.DATE_ONLY.name) }
            .getOrDefault(CalendarMode.DATE_ONLY)
    }

    fun setMode(context: Context, widgetId: Int, mode: CalendarMode) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString("mode_$widgetId", mode.name).apply()
    }

    fun onWidgetDeleted(context: Context, widgetId: Int) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().remove("mode_$widgetId").apply()
    }
}