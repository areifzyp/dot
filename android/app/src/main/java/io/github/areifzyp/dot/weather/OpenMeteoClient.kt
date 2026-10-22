package io.github.areifzyp.dot.weather

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

/**
 * Sumber data cuaca Open-Meteo — tanpa API key (PRD §6.3 CUA-6).
 */
object OpenMeteoClient {

    data class City(
        val name: String,
        val admin: String?,
        val country: String?,
        val lat: Double,
        val lon: Double,
    ) {
        val displayName: String
            get() = listOfNotNull(name, admin, country).distinct().joinToString(", ")
    }

    data class HourPoint(val hour: Int, val temp: Double)

    data class Forecast(
        val tempC: Double,
        val weatherCode: Int,
        val hourly: List<HourPoint>,
        val fetchedAt: Long,
    )

    /** CUA-1: pencarian kota secara manual. */
    suspend fun searchCities(query: String, limit: Int = 6): List<City> =
        withContext(Dispatchers.IO) {
            val q = URLEncoder.encode(query, "UTF-8")
            val json = httpGet(
                "https://geocoding-api.open-meteo.com/v1/search?name=$q&count=$limit&language=id&format=json"
            ) ?: return@withContext emptyList<City>()
            val results = JSONObject(json).optJSONArray("results") ?: return@withContext emptyList<City>()
            (0 until results.length()).mapNotNull { i ->
                val o = results.getJSONObject(i)
                val lat = o.optDouble("latitude", Double.NaN)
                val lon = o.optDouble("longitude", Double.NaN)
                if (lat.isNaN() || lon.isNaN()) return@mapNotNull null
                City(
                    name = o.optString("name"),
                    admin = o.optString("admin1").ifEmpty { null },
                    country = o.optString("country").ifEmpty { null },
                    lat = lat,
                    lon = lon,
                )
            }
        }

    /** CUA-2: suhu saat ini + kondisi; CUA-3: hourly untuk ukuran sedang. */
    suspend fun fetchForecast(lat: Double, lon: Double): Forecast =
        withContext(Dispatchers.IO) {
            val url = "https://api.open-meteo.com/v1/forecast" +
                "?latitude=$lat&longitude=$lon" +
                "&current=temperature_2m,weather_code" +
                "&hourly=temperature_2m&forecast_hours=6&timezone=auto"
            val json = httpGet(url) ?: throw java.io.IOException("Respons kosong dari Open-Meteo")
            val root = JSONObject(json)
            val current = root.getJSONObject("current")
            val tempC = current.getDouble("temperature_2m")
            val code = current.getInt("weather_code")

            val hourly = mutableListOf<HourPoint>()
            val hourlyObj = root.optJSONObject("hourly")
            if (hourlyObj != null) {
                val times = hourlyObj.optJSONArray("time")
                val temps = hourlyObj.optJSONArray("temperature_2m")
                if (times != null && temps != null) {
                    for (i in 0 until minOf(times.length(), temps.length())) {
                        // format ISO "2026-10-22T14:00"
                        val t = times.optString(i)
                        val hour = t.substringAfterLast('T').substringBefore(':').toIntOrNull() ?: continue
                        hourly += HourPoint(hour, temps.getDouble(i))
                    }
                }
            }
            Forecast(tempC = tempC, weatherCode = code, hourly = hourly, fetchedAt = System.currentTimeMillis())
        }

    private fun httpGet(url: String): String? {
        val conn = URL(url).openConnection() as HttpURLConnection
        return try {
            conn.connectTimeout = 10_000
            conn.readTimeout = 10_000
            conn.setRequestProperty("Accept", "application/json")
            if (conn.responseCode != 200) return null
            BufferedReader(InputStreamReader(conn.inputStream)).use { it.readText() }
        } finally {
            conn.disconnect()
        }
    }
}

/** Label kondisi WMO dalam Bahasa Indonesia (CUA-2). */
object WmoLabels {
    fun label(code: Int): String = when (code) {
        0 -> "Cerah"
        1 -> "Cerah Berawan"
        2 -> "Sebagian Berawan"
        3 -> "Berawan"
        45, 48 -> "Berkabut"
        51, 53, 55 -> "Gerimis"
        56, 57 -> "Gerimis Beku"
        61, 63, 65 -> "Hujan"
        66, 67 -> "Hujan Beku"
        71, 73, 75, 77 -> "Salju"
        80, 81, 82 -> "Hujan Lebat"
        85, 86 -> "Salju Lebat"
        95 -> "Badai Petir"
        96, 99 -> "Badai Petir + Es"
        else -> "Tidak Diketahui"
    }
}