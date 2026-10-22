package io.github.areifzyp.dot.gallery

import androidx.annotation.StringRes
import io.github.areifzyp.dot.R
import io.github.areifzyp.dot.widget.ClockWidgetReceiver

/**
 * Katalog widget untuk Galeri (PRD §6.1 GAL-1, GAL-5).
 * Fase 1: hanya Widget Jam yang dapat dipasang; sisanya berstatus "Segera hadir".
 */
enum class WidgetKind { CLOCK, WEATHER, CALENDAR, DEVICE, MUSIC }

enum class SizeVariant(@StringRes val labelRes: Int) {
    SMALL(R.string.size_small),
    MEDIUM(R.string.size_medium),
    LARGE(R.string.size_large),
}

data class WidgetEntry(
    val kind: WidgetKind,
    @StringRes val nameRes: Int,
    @StringRes val descRes: Int,
    val sizes: List<SizeVariant>,
    val available: Boolean,
) {
    companion object {
        val ALL = listOf(
            WidgetEntry(
                kind = WidgetKind.CLOCK,
                nameRes = R.string.catalog_clock_name,
                descRes = R.string.catalog_clock_desc,
                sizes = listOf(SizeVariant.SMALL, SizeVariant.MEDIUM, SizeVariant.LARGE),
                available = true,
            ),
            WidgetEntry(
                kind = WidgetKind.WEATHER,
                nameRes = R.string.catalog_weather_name,
                descRes = R.string.catalog_weather_desc,
                sizes = listOf(SizeVariant.SMALL, SizeVariant.MEDIUM),
                available = false,
            ),
            WidgetEntry(
                kind = WidgetKind.CALENDAR,
                nameRes = R.string.catalog_calendar_name,
                descRes = R.string.catalog_calendar_desc,
                sizes = listOf(SizeVariant.SMALL, SizeVariant.MEDIUM),
                available = false,
            ),
            WidgetEntry(
                kind = WidgetKind.DEVICE,
                nameRes = R.string.catalog_device_name,
                descRes = R.string.catalog_device_desc,
                sizes = listOf(SizeVariant.MEDIUM),
                available = false,
            ),
            WidgetEntry(
                kind = WidgetKind.MUSIC,
                nameRes = R.string.catalog_music_name,
                descRes = R.string.catalog_music_desc,
                sizes = listOf(SizeVariant.MEDIUM),
                available = false,
            ),
        )
    }
}

/**
 * GAL-3: memicu dialog pin widget bawaan sistem (API 26+).
 * Return false bila launcher tidak mendukung pemasangan langsung —
 * pemanggil wajib menampilkan panduan manual (PRD §6.1 kriteria penerimaan).
 */
object WidgetPinRequest {
    fun request(context: android.content.Context): Boolean {
        val awm = android.appwidget.AppWidgetManager.getInstance(context)
        if (!awm.isRequestPinAppWidgetSupported) return false
        val provider = android.content.ComponentName(context, ClockWidgetReceiver::class.java)
        val confirmation = android.app.PendingIntent.getBroadcast(
            context,
            0,
            android.content.Intent(context, ClockWidgetReceiver::class.java),
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE,
        )
        return try {
            awm.requestPinAppWidget(provider, null, confirmation)
        } catch (_: Exception) {
            false
        }
    }
}