package io.github.areifzyp.dot.widget

import android.content.Context

/**
 * Preferensi per-widget untuk Widget Teks (PRD §6.11).
 * Menyimpan teks, perataan, dan ukuran huruf per appWidgetId.
 */
enum class TextAlign { LEFT, CENTER, RIGHT }
enum class TextSize { SMALL, MEDIUM, LARGE }

object TextWidgetSettings {

    private const val PREFS = "dot_text_widget_prefs"
    private const val MAX_LENGTH = 100 // TXT-1: batas 100 karakter

    fun getText(context: Context, widgetId: Int): String =
        prefs(context).getString("text_$widgetId", "") ?: ""

    fun setText(context: Context, widgetId: Int, text: String) {
        prefs(context).edit().putString("text_$widgetId", text.take(MAX_LENGTH)).apply()
    }

    fun getAlign(context: Context, widgetId: Int): TextAlign {
        val raw = prefs(context).getString("align_$widgetId", TextAlign.CENTER.name)
        return runCatching { TextAlign.valueOf(raw ?: TextAlign.CENTER.name) }
            .getOrDefault(TextAlign.CENTER)
    }

    fun setAlign(context: Context, widgetId: Int, align: TextAlign) {
        prefs(context).edit().putString("align_$widgetId", align.name).apply()
    }

    fun getSize(context: Context, widgetId: Int): TextSize {
        val raw = prefs(context).getString("size_$widgetId", TextSize.MEDIUM.name)
        return runCatching { TextSize.valueOf(raw ?: TextSize.MEDIUM.name) }
            .getOrDefault(TextSize.MEDIUM)
    }

    fun setSize(context: Context, widgetId: Int, size: TextSize) {
        prefs(context).edit().putString("size_$widgetId", size.name).apply()
    }

    fun onWidgetDeleted(context: Context, widgetId: Int) {
        prefs(context).edit()
            .remove("text_$widgetId")
            .remove("align_$widgetId")
            .remove("size_$widgetId")
            .apply()
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}