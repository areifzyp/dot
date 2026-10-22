package io.github.areifzyp.dot.decorative

import android.content.Context
import android.content.SharedPreferences

/**
 * Penyimpanan pola Dekoratif per widgetId (PRD §6.7 DEK-2, §6.9 KON-4).
 */
object DecorSettings {
    private const val PREF_FILE = "dot_decor_settings"
    private const val KEY_PATTERN = "pattern"

    fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

    fun save(context: Context, widgetId: Int, pattern: DecorPattern) {
        prefs(context).edit().putString(key(widgetId, KEY_PATTERN), pattern.name).apply()
    }

    fun load(context: Context, widgetId: Int): DecorPattern {
        val name = prefs(context).getString(key(widgetId, KEY_PATTERN), null)
        return name?.let { runCatching { DecorPattern.valueOf(it) }.getOrNull() }
            ?: DecorPattern.FULL
    }

    fun onWidgetDeleted(context: Context, widgetId: Int) {
        prefs(context).edit().remove(key(widgetId, KEY_PATTERN)).apply()
    }

    private fun key(widgetId: Int, suffix: String) = "w${widgetId}_$suffix"
}