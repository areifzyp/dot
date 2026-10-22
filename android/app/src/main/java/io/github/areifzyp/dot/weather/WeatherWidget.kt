package io.github.areifzyp.dot.weather

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import io.github.areifzyp.dot.widget.appWidgetIdOrNull
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import io.github.areifzyp.dot.R
import io.github.areifzyp.dot.ui.theme.DotDarkBackground
import io.github.areifzyp.dot.ui.theme.DotDarkForeground
import io.github.areifzyp.dot.ui.theme.DotLightBackground
import io.github.areifzyp.dot.ui.theme.DotLightForeground
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Widget Cuaca — PRD §6.3 (CUA-1..5).
 * CUA-2: suhu + kondisi kota terpilih.
 * CUA-3: ukuran sedang menampilkan deretan suhu per jam.
 * CUA-4: refresh periodik via WorkManager (WeatherRefreshWorker).
 * Kriteria penerimaan: tanpa koneksi tampilkan data terakhir + waktu ambil.
 */
class WeatherWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetId = id.appWidgetIdOrNull()
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
        provideContent {
            WeatherWidgetContent(widgetId = widgetId)
        }
    }
}

private val WtrBackground: ColorProvider =
    io.github.areifzyp.dot.widget.DayNightColor(DotLightBackground, DotDarkBackground)
private val WtrForeground: ColorProvider =
    io.github.areifzyp.dot.widget.DayNightColor(DotLightForeground, DotDarkForeground)
private val WtrSecondary: ColorProvider =
    io.github.areifzyp.dot.widget.DayNightColor(
        day = DotLightForeground.copy(alpha = 0.6f),
        night = DotDarkForeground.copy(alpha = 0.6f),
    )

@Composable
private fun WeatherWidgetContent(widgetId: Int) {
    val context = LocalContext.current
    val cityName = WeatherSettings.getCityName(context, widgetId)
    val unit = WeatherSettings.getUnit(context, widgetId)
    val forecast = WeatherSettings.loadForecast(context, widgetId)

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(WtrBackground)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (cityName.isEmpty()) {
            // CUA-1: kota belum dipilih
            Text(
                text = context.getString(R.string.weather_no_city),
                style = TextStyle(color = WtrSecondary, fontSize = 12.sp, textAlign = TextAlign.Center),
                modifier = GlanceModifier.fillMaxWidth(),
            )
            return@Column
        }

        Text(
            text = cityName,
            style = TextStyle(color = WtrSecondary, fontSize = 12.sp, textAlign = TextAlign.Center),
            modifier = GlanceModifier.fillMaxWidth(),
        )

        if (forecast == null) {
            Text(
                text = context.getString(R.string.weather_no_data),
                style = TextStyle(color = WtrForeground, fontSize = 16.sp, textAlign = TextAlign.Center),
                modifier = GlanceModifier.fillMaxWidth(),
            )
            return@Column
        }

        // CUA-2: suhu besar + label kondisi
        Text(
            text = WeatherSettings.displayTemp(forecast.tempC, unit),
            style = TextStyle(color = WtrForeground, fontWeight = FontWeight.Bold, fontSize = 32.sp, textAlign = TextAlign.Center),
            modifier = GlanceModifier.fillMaxWidth(),
        )
        Text(
            text = WmoLabels.label(forecast.weatherCode),
            style = TextStyle(color = WtrSecondary, fontSize = 13.sp, textAlign = TextAlign.Center),
            modifier = GlanceModifier.fillMaxWidth(),
        )

        // CUA-3: deretan suhu per jam (hanya bila ada data hourly)
        if (forecast.hourly.isNotEmpty()) {
            Spacer(GlanceModifier.height(8.dp))
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
            ) {
                forecast.hourly.take(5).forEach { pt ->
                    Column(
                        // Glance 1.1.0 belum punya weight — pakai lebar tetap
                        // (5 kolom × 36dp = 180dp = minWidth widget)
                        modifier = GlanceModifier.width(36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "%02d".format(pt.hour),
                            style = TextStyle(color = WtrSecondary, fontSize = 10.sp),
                        )
                        Text(
                            text = WeatherSettings.displayTemp(pt.temp, unit),
                            style = TextStyle(color = WtrForeground, fontSize = 11.sp),
                        )
                    }
                }
            }
        }

        Spacer(GlanceModifier.height(6.dp))
        Text(
            text = context.getString(
                R.string.weather_updated_at,
                DateTimeFormatter.ofPattern("HH:mm").format(
                    Instant.ofEpochMilli(forecast.fetchedAt).atZone(ZoneId.systemDefault()),
                ),
            ),
            style = TextStyle(color = WtrSecondary, fontSize = 10.sp, textAlign = TextAlign.Center),
            modifier = GlanceModifier.fillMaxWidth(),
        )
    }
}

class WeatherWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WeatherWidget()

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        appWidgetIds.forEach { WeatherSettings.onWidgetDeleted(context, it) }
    }
}