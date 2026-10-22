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
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
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

    val density = context.resources.displayMetrics.density
    val sizeDp = LocalSize.current
    val padDp = 14f
    val availWdp = if (sizeDp.width.value.isFinite() && sizeDp.width.value > 60f) {
        sizeDp.width.value - padDp * 2
    } else {
        250f - padDp * 2
    }
    val availHdp = if (sizeDp.height.value.isFinite() && sizeDp.height.value > 60f) {
        sizeDp.height.value - padDp * 2
    } else {
        110f - padDp * 2
    }

    val labelSp = 12f
    val labelPx = labelSp * context.resources.displayMetrics.scaledDensity
    val matrixHdp = (availHdp - labelSp * 1.7f - 6f).coerceAtLeast(20f)

    val onColor = WidgetForeground.getColor(context).toArgb()
    val offColor = WidgetInactiveDot.getColor(context).toArgb()
    val labelColor = WidgetSecondary.getColor(context).toArgb()

    val matrix = remember(glyphs, availWdp, matrixHdp, onColor, offColor) {
        DotRender.dotMatrix(
            glyphs = glyphs,
            maxWidthPx = (availWdp * density).toInt().coerceAtLeast(32),
            maxHeightPx = (matrixHdp * density).toInt().coerceAtLeast(32),
            onColor = onColor,
            offColor = offColor,
        )
    }

    val dateText = LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, d MMM"))
    val dateLabel = if (amPm != null) "${dateText.uppercase()} · $amPm" else dateText.uppercase()
    val label = remember(dateLabel, labelPx, labelColor) {
        DotRender.text(
            text = dateLabel,
            typeface = DotRender.typeface(context),
            textSizePx = labelPx,
            color = labelColor,
            letterSpacingEm = 0.14f,
        )
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(16.dp)
            .background(WidgetBackground)
            .padding(padDp.dp)
            .clickable(actionStartActivity(WidgetIntents.clock(LocalContext.current))),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = GlanceModifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // JAM-1: waktu sebagai dot-matrix (satu bitmap, bukan ratusan view)
            Image(
                provider = ImageProvider(matrix),
                contentDescription = "Jam",
                contentScale = ContentScale.Fit,
                modifier = GlanceModifier
                    .width((matrix.width / density).dp)
                    .height((matrix.height / density).dp),
            )

            Spacer(modifier = GlanceModifier.height(6.dp))

            // JAM-2: baris tanggal (+ AM/PM saat format 12 jam), digambar dengan Doto
            Image(
                provider = ImageProvider(label),
                contentDescription = dateLabel,
                contentScale = ContentScale.Fit,
                modifier = GlanceModifier
                    .width((label.width / density).dp)
                    .height((label.height / density).dp),
            )
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
        val intent = Intent(context, ClockWidgetReceiver::class.java)
            .setAction(ACTION_UPDATE_CLOCK)
        val pi = PendingIntent.getBroadcast(
            context, REQUEST_CODE, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerAt = SystemClock.elapsedRealtime() + millisToNextMinute()
        val exactAllowed = Build.VERSION.SDK_INT < Build.VERSION_CODES.S || am.canScheduleExactAlarms()
        if (exactAllowed) {
            try {
                am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi)
                return
            } catch (_: SecurityException) {
                // jatuh ke alarm inexact di bawah
            }
        }
        // Izin exact alarm dicabut (Android 12+): jangan berhenti menjadwalkan —
        // alarm inexact tetap menjaga jam berganti menit, hanya bisa meleset beberapa detik.
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi)
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