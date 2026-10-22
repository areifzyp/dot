package io.github.areifzyp.dot.style

import android.content.Context
import android.content.SharedPreferences
import android.os.Build

/**
 * Preferensi Gaya Nothing global (PRD §6.8 GAY-1b/2/3, aturan §8.5).
 *
 * Disimpan di satu SharedPreferences bersama (bukan per-widgetId) karena
 * gaya berlaku untuk SELURUH aplikasi dan semua widget (GAY-1b).
 * Default = palet dasar Nothing: tema Ikuti Sistem, font Doto, aksen merah.
 */
object DotStyleSettings {
    private const val PREF_FILE = "dot_style_settings"

    private const val KEY_THEME = "theme_mode"
    private const val KEY_FONT = "font_choice"
    private const val KEY_ACCENT = "accent_choice"
    private const val KEY_UPDATE_INTERVAL = "update_interval_minutes"

    private const val DEFAULT_UPDATE_INTERVAL = 30

    enum class ThemeMode { SYSTEM, LIGHT, DARK }
    enum class FontChoice { DOTO, SYSTEM_SANS }
    enum class AccentChoice { NOTHING_RED, PRESET_ORANGE, SYSTEM_MATERIAL }

    fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)

    fun getTheme(context: Context): ThemeMode =
        enumOrDefault(prefs(context).getString(KEY_THEME, null), ThemeMode.SYSTEM)

    fun setTheme(context: Context, mode: ThemeMode) {
        prefs(context).edit().putString(KEY_THEME, mode.name).apply()
    }

    fun getFont(context: Context): FontChoice =
        enumOrDefault(prefs(context).getString(KEY_FONT, null), FontChoice.DOTO)

    fun setFont(context: Context, font: FontChoice) {
        prefs(context).edit().putString(KEY_FONT, font.name).apply()
    }

    fun getAccent(context: Context): AccentChoice {
        val saved = enumOrDefault(prefs(context).getString(KEY_ACCENT, null), AccentChoice.NOTHING_RED)
        // Aturan §8.5: Warna Sistem hanya opsi di API 31+; di bawah itu kembali ke default merah.
        return if (saved == AccentChoice.SYSTEM_MATERIAL && Build.VERSION.SDK_INT < 31) {
            AccentChoice.NOTHING_RED
        } else {
            saved
        }
    }

    fun setAccent(context: Context, accent: AccentChoice) {
        prefs(context).edit().putString(KEY_ACCENT, accent.name).apply()
    }

    /** Interval minimum antar-update perangkat (menit). Default 30 (PRD §6.5 STA). */
    fun getUpdateIntervalMinutes(context: Context): Int =
        prefs(context).getInt(KEY_UPDATE_INTERVAL, DEFAULT_UPDATE_INTERVAL)

    fun setUpdateIntervalMinutes(context: Context, minutes: Int) {
        prefs(context).edit().putInt(KEY_UPDATE_INTERVAL, minutes).apply()
    }

    /** Opsi aksen yang boleh ditampilkan di UI (menyembunyikan Material You di API <31). */
    fun availableAccents(): List<AccentChoice> = buildList {
        add(AccentChoice.NOTHING_RED)
        add(AccentChoice.PRESET_ORANGE)
        if (Build.VERSION.SDK_INT >= 31) add(AccentChoice.SYSTEM_MATERIAL)
    }

    private inline fun <reified T : Enum<T>> enumOrDefault(value: String?, fallback: T): T =
        value?.let { runCatching { enumValueOf<T>(it) }.getOrNull() } ?: fallback
}