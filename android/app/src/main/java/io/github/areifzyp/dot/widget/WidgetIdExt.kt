package io.github.areifzyp.dot.widget

import androidx.glance.GlanceId
import androidx.glance.appwidget.AppWidgetId

/**
 * Centrale helper untuk membaca integer [AppWidgetManager.appWidgetId] dari [GlanceId].
 *
 * `AppWidgetId` dan propertinya di-flag [androidx.annotation.RestrictedApi] pada Glance 1.1.0,
 * sehingga lint menolak penggunaan langsung. Karena kita membutuhkannya untuk per-widget
 * persistence (mode kalender, format jam, lokasi cuaca, pola dekoratif), helper ini
 * membungkus suppress di satu tempat — call site jadi bersih dan tidak perlu annotation berulang.
 *
 * Catatan: nilai yang dikembalikan stabil untuk satu widget instance selama widget itu ada.
 */
@Suppress("RestrictedApi")
internal fun GlanceId.appWidgetIdOrNull(): Int? = (this as? AppWidgetId)?.appWidgetId

@Suppress("RestrictedApi")
internal fun Int.toGlanceAppWidgetId(): AppWidgetId = AppWidgetId(this)