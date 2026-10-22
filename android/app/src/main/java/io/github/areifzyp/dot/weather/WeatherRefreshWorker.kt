package io.github.areifzyp.dot.weather

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.CoroutineWorker
import java.time.Duration

/**
 * CUA-4: refresh berkala (interval 1 jam, butuh jaringan).
 * Gagal fetch → tetap tampilkan cache (kriteria penerimaan PRD §6.3).
 */
class WeatherRefreshWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(applicationContext)
        val ids = manager.getGlanceIds(WeatherWidget::class.java)
        if (ids.isEmpty()) return Result.success()

        var anyFailure = false
        ids.forEach { glanceId ->
            val widgetId = (glanceId as? androidx.glance.appwidget.AppWidgetId)?.appWidgetId
                ?: AppWidgetManager.INVALID_APPWIDGET_ID
            val coords = WeatherSettings.getCoords(applicationContext, widgetId)
                ?: return@forEach
            try {
                val forecast = OpenMeteoClient.fetchForecast(coords.first, coords.second)
                WeatherSettings.saveForecast(applicationContext, widgetId, forecast)
            } catch (_: Exception) {
                anyFailure = true
            }
            WeatherWidget().update(applicationContext, glanceId)
        }
        // Retry dengan backoff bila ada kegagalan jaringan
        return if (anyFailure) Result.retry() else Result.success()
    }

    companion object {
        private const val WORK_NAME = "dot-weather-refresh"

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<WeatherRefreshWorker>(Duration.ofHours(1))
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build(),
                )
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }

        fun refreshNow(context: Context) {
            WorkManager.getInstance(context).enqueue(
                PeriodicWorkRequestBuilder<WeatherRefreshWorker>(Duration.ofHours(1)).build(),
            )
        }

        fun cancelIfNoWidgets(context: Context) {
            val awm = AppWidgetManager.getInstance(context)
            val ids = awm.getAppWidgetIds(
                ComponentName(context, WeatherWidgetReceiver::class.java),
            )
            if (ids.isEmpty()) {
                WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            }
        }
    }
}