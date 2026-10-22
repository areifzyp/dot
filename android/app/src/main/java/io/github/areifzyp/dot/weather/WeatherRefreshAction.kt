package io.github.areifzyp.dot.weather

import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback

/**
 * KON-4: ketuk Widget Cuaca menyegarkan data sekarang (di luar jadwal per jam).
 * Tetap memakai WorkManager supaya fetch berjalan di luar main thread dan
 * tahan bila proses widget dimatikan sistem.
 */
class WeatherRefreshAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        WeatherRefreshWorker.refreshNow(context)
    }
}
