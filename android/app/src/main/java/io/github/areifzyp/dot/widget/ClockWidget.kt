package io.github.areifzyp.dot.widget

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.SystemClock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import io.github.areifzyp.dot.ui.theme.DotDarkBackground
import io.github.areifzyp.dot.ui.theme.DotDarkForeground
import io.github.areifzyp.dot.ui.theme.DotDarkInactiveDot
import io.github.areifzyp.dot.ui.theme.DotLightBackground
import io.github.areifzyp.dot.ui.theme.DotLightForeground
import io.github.areifzyp.dot.ui.theme.DotLightInactiveDot
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Dot. Clock Widget — PRD §5 (JAM-1, JAM-2).
 * Renders current time as dot-matrix digits (5x7 grid per glyph).
 * Tema terang/gelap otomatis via ColorProvider berbasis uiMode.
 * Update tiap menit via AlarmManager exact alarm (ClockUpdateScheduler),
 * karena updatePeriodMillis dibulatkan sistem ke minimum ~30 menit.
 */
class ClockWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetId = id.appWidgetIdOrNull()
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
        provideContent {
            ClockWidgetContent(widgetId = widgetId)
        }
    }
}

// Token warna widget: day/night pair (design/tokens.json §color.theme)
private val WidgetForeground: ColorProvider = DayNightColor(DotLightForeground, DotDarkForeground)
private val WidgetBackground: ColorProvider = DayNightColor(DotLightBackground, DotDarkBackground)
private val WidgetInactiveDot: ColorProvider = DayNightColor(DotLightInactiveDot, DotDarkInactiveDot)
private val WidgetSecondary: ColorProvider = DayNightColor(
    day = DotLightForeground.copy(alpha = 0.6f),
    night = DotDarkForeground.copy(alpha = 0.6f),
)

@Composable
private fun ClockWidgetContent(widgetId: Int) {
    val context = LocalContext.current
    val now = LocalTime.now()
    val format = DotSettings.getTimeFormat(context, widgetId)
    val use24Hour = DotSettings.is24Hour(context, format)
    val glyphs = DotMatrix.timeToGlyphs(now.hour, now.minute, use24Hour = use24Hour)
    val amPm = if (!use24Hour) if (now.hour < 12) "AM" else "PM" else null

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(WidgetBackground)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // JAM-1: waktu sebagai dot-matrix
            DotMatrixTimeDisplay(glyphs = glyphs)

            Spacer(modifier = GlanceModifier.height(8.dp))

            // JAM-2: baris tanggal (+ indikator AM/PM saat format 12 jam)
            val dateText = LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, d MMM"))
            val dateLabel = if (amPm != null) "${dateText.uppercase()} · $amPm" else dateText.uppercase()
            Text(
                text = dateLabel,
                style = TextStyle(
                    color = WidgetSecondary,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                ),
            )
        }
    }
}

@Composable
private fun DotMatrixTimeDisplay(glyphs: List<Pair<String, List<Int>>>) {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        glyphs.forEachIndexed { index, (label, pattern) ->
            if (label == ":") {
                ColonDots()
            } else {
                DigitGrid(pattern = pattern)
            }
            if (index < glyphs.lastIndex) {
                Spacer(modifier = GlanceModifier.width(4.dp))
            }
        }
    }
}

@Composable
private fun DigitGrid(pattern: List<Int>) {
    val dotSize = 6.dp
    val dotSpacing = 2.dp

    Column {
        for (row in 0 until DotMatrix.ROWS) {
            Row {
                for (col in 0 until DotMatrix.COLS) {
                    val isOn = DotMatrix.isDotOn(pattern, col, row)
                    Spacer(
                        modifier = GlanceModifier
                            .size(dotSize)
                            .background(if (isOn) WidgetForeground else WidgetInactiveDot)
                    )
                    if (col < DotMatrix.COLS - 1) {
                        Spacer(modifier = GlanceModifier.width(dotSpacing))
                    }
                }
            }
            if (row < DotMatrix.ROWS - 1) {
                Spacer(modifier = GlanceModifier.height(dotSpacing))
            }
        }
    }
}

@Composable
private fun ColonDots() {
    val dotSize = 6.dp
    val dotSpacing = 2.dp

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        for (row in 0 until DotMatrix.ROWS) {
            val isOn = DotMatrix.isDotOn(DotMatrix.COLON, 0, row)
            Spacer(
                modifier = GlanceModifier
                    .size(dotSize)
                    .background(if (isOn) WidgetForeground else WidgetBackground)
            )
            if (row < DotMatrix.ROWS - 1) {
                Spacer(modifier = GlanceModifier.height(dotSpacing))
            }
        }
    }
}

/**
 * Spike PRD Fase 1 tugas 4: mekanisme update tiap menit.
 * updatePeriodMillis tidak bisa diandalkan (dibulatkan OS ke ~30 menit),
 * jadi widget menjadwalkan exact alarm ke batas menit berikutnya; tiap kali
 * alarm berbunyi widget di-recompose lalu jadwal berikutnya dipasang lagi.
 * Perlu USE_EXACT_ALARM (di-grant otomatis untuk aplikasi jam/alarm, API 33+).
 */
object ClockUpdateScheduler {
    const val ACTION_UPDATE_CLOCK = "io.github.areifzyp.dot.widget.ACTION_UPDATE_CLOCK"
    private const val REQUEST_CODE = 42

    /** Milidetik menuju batas menit berikutnya. Tepat di batas menit -> 60000. */
    fun millisToNextMinute(nowMillis: Long = System.currentTimeMillis()): Long {
        val minuteMs = 60_000L
        return minuteMs - (nowMillis % minuteMs)
    }

    fun scheduleNextUpdate(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !am.canScheduleExactAlarms()) {
            return // exact alarm dicabut user; fallback = updatePeriodMillis
        }
        val intent = Intent(context, ClockWidgetReceiver::class.java)
            .setAction(ACTION_UPDATE_CLOCK)
        val pi = PendingIntent.getBroadcast(
            context, REQUEST_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerAt = SystemClock.elapsedRealtime() + millisToNextMinute()
        try {
            am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi)
        } catch (_: SecurityException) {
            am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi)
        }
    }

    fun cancel(context: Context) {
        val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ClockWidgetReceiver::class.java)
            .setAction(ACTION_UPDATE_CLOCK)
        val pi = PendingIntent.getBroadcast(
            context, REQUEST_CODE, intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pi != null) {
            am.cancel(pi)
            pi.cancel()
        }
    }
}

class ClockWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ClockWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        ClockUpdateScheduler.scheduleNextUpdate(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        ClockUpdateScheduler.cancel(context)
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        super.onDeleted(context, appWidgetIds)
        appWidgetIds.forEach { DotSettings.onWidgetDeleted(context, it) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == ClockUpdateScheduler.ACTION_UPDATE_CLOCK) {
            val pending = goAsync()
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    this@ClockWidgetReceiver.glanceAppWidget.updateAll(context)
                    ClockUpdateScheduler.scheduleNextUpdate(context)
                } finally {
                    pending.finish()
                }
            }
        }
    }
}