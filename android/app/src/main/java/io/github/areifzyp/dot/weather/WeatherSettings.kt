package io.github.areifzyp.dot.weather

import android.content.Context

/**
 * Preferensi per-widget Widget Cuaca (PRD §6.3):
 * kota terpilih, satuan suhu, dan cache forecast terakhir
 * (kriteria penerimaan: tanpa koneksi tampilkan data terakhir + waktunya).
 */
enum class TempUnit { C, F }

object WeatherSettings {

    private const val PREFS = "dot_weather_widget_prefs"

    fun getCityName(context: Context, widgetId: Int): String =
        prefs(context).getString("city_$widgetId", "") ?: ""

    fun getCoords(context: Context, widgetId: Int): Pair<Double, Double>? {
        val raw = prefs(context).getString("coords_$widgetId", null) ?: return null
        val parts = raw.split("|")
        if (parts.size != 2) return null
        val lat = parts[0].toDoubleOrNull() ?: return null
        val lon = parts[1].toDoubleOrNull() ?: return null
        return lat to lon
    }

    fun setCity(context: Context, widgetId: Int, name: String, lat: Double, lon: Double) {
        prefs(context).edit()
            .putString("city_$widgetId", name)
            .putString("coords_$widgetId", "$lat|$lon")
            .apply()
    }

    fun getUnit(context: Context, widgetId: Int): TempUnit {
        val raw = prefs(context).getString("unit_$widgetId", TempUnit.C.name)
        return runCatching { TempUnit.valueOf(raw ?: TempUnit.C.name) }.getOrDefault(TempUnit.C)
    }

    fun setUnit(context: Context, widgetId: Int, unit: TempUnit) {
        prefs(context).edit().putString("unit_$widgetId", unit.name).apply()
    }

    // ---- Cache forecast terakhir ----

    fun saveForecast(context: Context, widgetId: Int, f: OpenMeteoClient.Forecast) {
        val hourly = f.hourly.joinToString(",") { "${it.hour}:${it.temp}" }
        prefs(context).edit()
            .putFloat("temp_$widgetId", f.tempC.toFloat())
            .putInt("code_$widgetId", f.weatherCode)
            .putString("hourly_$widgetId", hourly)
            .putLong("fetched_$widgetId", f.fetchedAt)
            .apply()
    }

    fun loadForecast(context: Context, widgetId: Int): OpenMeteoClient.Forecast? {
        val p = prefs(context)
        if (!p.contains("fetched_$widgetId")) return null
        val hourly = (p.getString("hourly_$widgetId", "") ?: "")
            .split(",").filter { it.isNotEmpty() }
            .mapNotNull {
                val kv = it.split(":")
                if (kv.size != 2) return@mapNotNull null
                val h = kv[0].toIntOrNull() ?: return@mapNotNull null
                val t = kv[1].toDoubleOrNull() ?: return@mapNotNull null
                OpenMeteoClient.HourPoint(h, t)
            }
        return OpenMeteoClient.Forecast(
            tempC = p.getFloat("temp_$widgetId", 0f).toDouble(),
            weatherCode = p.getInt("code_$widgetId", 0),
            hourly = hourly,
            fetchedAt = p.getLong("fetched_$widgetId", 0L),
        )
    }

    fun onWidgetDeleted(context: Context, widgetId: Int) {
        prefs(context).edit()
            .remove("city_$widgetId").remove("coords_$widgetId")
            .remove("unit_$widgetId")
            .remove("temp_$widgetId").remove("code_$widgetId")
            .remove("hourly_$widgetId").remove("fetched_$widgetId")
            .apply()
    }

    fun displayTemp(tempC: Double, unit: TempUnit): String = when (unit) {
        TempUnit.C -> "${Math.round(tempC)}°C"
        TempUnit.F -> "${Math.round(tempC * 9 / 5 + 32)}°F"
    }

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}