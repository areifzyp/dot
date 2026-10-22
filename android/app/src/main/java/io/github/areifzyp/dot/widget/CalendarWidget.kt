package io.github.areifzyp.dot.widget

import android.Manifest
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
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
import io.github.areifzyp.dot.ui.theme.DotDarkBackground
import io.github.areifzyp.dot.ui.theme.DotDarkForeground
import io.github.areifzyp.dot.ui.theme.DotLightBackground
import io.github.areifzyp.dot.ui.theme.DotLightForeground
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Widget Kalender — PRD §6.4 (KAL-1 tanggal, KAL-2 agenda, KAL-3 mode tampilan).
 * Mode DATE_ONLY berfungsi tanpa izin; DATE_AGENDA membaca CalendarContract
 * hanya bila READ_CALENDAR sudah di-grant.
 */
class CalendarWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetId = id.appWidgetIdOrNull()
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
        val mode = CalendarWidgetSettings.getMode(context, widgetId)
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_CALENDAR,
        ) == PackageManager.PERMISSION_GRANTED
        val events = if (mode == CalendarMode.DATE_AGENDA && hasPermission) {
            withContext(Dispatchers.IO) {
                CalendarEvents.upcoming(context, WidgetClock.millis())
            }
        } else {
            emptyList()
        }
        provideContent {
            CalendarWidgetContent(mode = mode, events = events, hasPermission = hasPermission)
        }
    }
}

private val CalBackground: ColorProvider = DayNightColor(DotLightBackground, DotDarkBackground)
private val CalForeground: ColorProvider = DayNightColor(DotLightForeground, DotDarkForeground)
private val CalSecondary: ColorProvider = DayNightColor(
    day = DotLightForeground.copy(alpha = 0.6f),
    night = DotDarkForeground.copy(alpha = 0.6f),
)

private val idLocale = Locale("id", "ID")

@Composable
private fun CalendarWidgetContent(
    mode: CalendarMode,
    events: List<CalendarEvents.Event>,
    hasPermission: Boolean,
) {
    val today = WidgetClock.today()
    val dayName = today.format(DateTimeFormatter.ofPattern("EEEE", idLocale)).uppercase()
    val dateText = today.format(DateTimeFormatter.ofPattern("d MMMM yyyy", idLocale))

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(16.dp).background(CalBackground)
            .padding(12.dp)
            .clickable(actionStartActivity(WidgetIntents.calendar(LocalContext.current))),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // KAL-1: tanggal hari ini
        Text(
            text = dayName,
            style = TextStyle(
                color = CalForeground,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
            ),
            modifier = GlanceModifier.fillMaxWidth(),
        )
        Text(
            text = dateText,
            style = TextStyle(color = CalSecondary, fontSize = 12.sp, textAlign = TextAlign.Center),
            modifier = GlanceModifier.fillMaxWidth(),
        )

        if (mode == CalendarMode.DATE_AGENDA) {
            Spacer(GlanceModifier.height(8.dp))
            if (!hasPermission) {
                Text(
                    text = LocalContext.current.getString(
                        io.github.areifzyp.dot.R.string.calendar_no_permission,
                    ),
                    style = TextStyle(color = CalSecondary, fontSize = 11.sp, textAlign = TextAlign.Center),
                    modifier = GlanceModifier.fillMaxWidth(),
                )
            } else if (events.isEmpty()) {
                // Kriteria penerimaan: keterangan kosong yang jelas
                Text(
                    text = LocalContext.current.getString(
                        io.github.areifzyp.dot.R.string.calendar_empty,
                    ),
                    style = TextStyle(color = CalSecondary, fontSize = 11.sp, textAlign = TextAlign.Center),
                    modifier = GlanceModifier.fillMaxWidth(),
                )
            } else {
                // KAL-2: agenda terdekat (maks 3)
                events.forEach { ev ->
                    Row(
                        modifier = GlanceModifier.fillMaxWidth().padding(vertical = 1.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = formatTime(ev.startMillis),
                            style = TextStyle(color = CalSecondary, fontSize = 11.sp),
                        )
                        Spacer(GlanceModifier.width(6.dp))
                        Text(
                            text = ev.title,
                            style = TextStyle(color = CalForeground, fontSize = 11.sp),
                            maxLines = 1,
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(millis: Long): String =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ofPattern("HH:mm"))

class CalendarWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = CalendarWidget()

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        appWidgetIds.forEach { CalendarWidgetSettings.onWidgetDeleted(context, it) }
    }
}