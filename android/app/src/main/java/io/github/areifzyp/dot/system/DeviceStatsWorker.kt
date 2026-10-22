package io.github.areifzyp.dot.system

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.CoroutineWorker
import io.github.areifzyp.dot.style.DotStyleSettings
import java.time.Duration

/**
 * HP-5: penyegaran terjadwal tiap 15 menit.
 *
 * WorkManager minimum interval periodik adalah 15 menit, jadi cocok pas dengan
 * kebutuhan PRD. Perubahan baterai signifikan ditangani terpisah oleh
 * [DeviceStatusWidgetReceiver] lewat broadcast sistem (lebih cepat dari jadwal).
 */
class DeviceStatsWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(applicationContext)
        val ids = manager.getGlanceIds(DeviceStatusWidget::class.java)
        ids.forEach { glanceId ->
            try {
                DeviceStatusWidget().update(applicationContext, glanceId)
            } catch (_: Exception) {
                // widget individual gagal update; coba jadwal berikutnya
            }
        }
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "dot-device-stats-refresh"

        fun schedule(context: Context) {
            val minutes = DotStyleSettings.getUpdateIntervalMinutes(context).coerceAtLeast(15)
            val request = PeriodicWorkRequestBuilder<DeviceStatsWorker>(Duration.ofMinutes(minutes.toLong()))
                .setConstraints(Constraints.Builder().build())
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }

        fun cancelIfNoWidgets(context: Context) {
            val awm = android.appwidget.AppWidgetManager.getInstance(context)
            val ids = awm.getAppWidgetIds(
                android.content.ComponentName(context, DeviceStatusWidgetReceiver::class.java),
            )
            if (ids.isEmpty()) {
                WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            }
        }
    }
}