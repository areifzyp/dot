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
import androidx.glance.layout.Row
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
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Dot. Clock Widget — PRD §5 (JAM-1, JAM-2).
 * Renders current time as dot-matrix digits (5x7 grid per glyph).
 * Tema terang/gelap otomatis via ColorProvider berbasis uiMode.
 * Update tiap menit via AlarmManager exact alarm (ClockUpdateScheduler),
 * karena updatePeriodMillis dibulatkan sistem ke minimum ~30 menit.
 *
 * STATUS-MAP §4a: bila izin exact alarm dicabut, alarm menjadi inexact dan
 * pembaruan bisa terlambat. Supaya widget tidak pernah *tampak* diam tanpa
 * memberi tahu, setiap render membandingkan stempel pembaruan terakhir dengan
 * jam berjalan (ClockFreshnessPolicy) dan menampilkan penanda "TERTUNDA HH:MM"
 * saat selisihnya ≥ 2 menit.
 */
class ClockWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetId = id.appWidgetIdOrNull()
            ?: AppWidgetManager.INVALID_APPWIDGET_ID

        // Satu instan waktu per render: digit jam, label tanggal, dan stempel
        // memakai nilai yang sama supaya "menit yang ditampilkan" bisa
        // dibandingkan dengan menit berjalan (UX-1).
        val nowMillis = WidgetClock.millis()
        val now = Instant.ofEpochMilli(nowMillis)
            .atZone(WidgetClock.zone())
            .toLocalTime()
        val minuteOfDay = ClockFreshnessPolicy.minuteOfDay(now)

        // Stempel pembaruan *sebelumnya* yang menentukan status; setelah itu
        // stempel diganti, sehingga penanda hilang pada render berikutnya (UX-4).
        val previous = ClockFreshnessStore.load(context)
        val freshness = previous?.let { ClockFreshnessPolicy.evaluate(it, nowMillis) }
            ?: ClockFreshness.OnTime
        ClockFreshnessStore.save(context, ClockUpdateStamp(nowMillis, minuteOfDay))

        provideContent {
            ClockWidgetContent(widgetId = widgetId, now = now, freshness = freshness)
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

/**
 * Stempel pembaruan terakhir widget jam (STATUS-MAP §4a).
 *
 * Global, bukan per appwidget ID: semua widget jam digambar ulang oleh alarm
 * yang sama (satu PendingIntent, satu REQUEST_CODE), jadi stempelnya pun satu.
 * Disimpan di prefs yang sama dengan pengaturan widget supaya tidak menambah
 * berkas preferensi baru.
 */
internal object ClockFreshnessStore {

    private const val PREFS = "dot_widget_prefs"
    private const val KEY_EPOCH_MS = "clock_last_update_epoch_ms"
    private const val KEY_MINUTE_OF_DAY = "clock_last_update_minute_of_day"

    /** `null` bila widget belum pernah render (mis. baru dipasang) — jangan tampilkan penanda. */
    fun load(context: Context): ClockUpdateStamp? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val epochMillis = prefs.getLong(KEY_EPOCH_MS, -1L)
        val minuteOfDay = prefs.getInt(KEY_MINUTE_OF_DAY, -1)
        if (epochMillis <= 0L || minuteOfDay !in 0 until ClockFreshnessPolicy.MINUTES_PER_DAY) {
            return null
        }
        return ClockUpdateStamp(epochMillis = epochMillis, minuteOfDay = minuteOfDay)
    }

    fun save(context: Context, stamp: ClockUpdateStamp) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putLong(KEY_EPOCH_MS, stamp.epochMillis)
            .putInt(KEY_MINUTE_OF_DAY, stamp.minuteOfDay)
            .apply()
    }
}

@Composable
private fun ClockWidgetContent(
    widgetId: Int,
    now: LocalTime,
    freshness: ClockFreshness,
) {
    val context = LocalContext.current
    val format = DotSettings.getTimeFormat(context, widgetId)
    val use24Hour = DotSettings.is24Hour(context, format)
    val glyphs = DotMatrix.timeToGlyphs(now.hour, now.minute, use24Hour = use24Hour)
    val amPm = if (!use24Hour) if (now.hour < 12) "AM" else "PM" else null

    val density = context.resources.displayMetrics.density
    val sizeDp = LocalSize.current
    val padDp = ClockLayout.PAD_DP
    val availWdp = ClockLayout.availableWidthDp(sizeDp.width.value)
    val availHdp = ClockLayout.availableHeightDp(sizeDp.height.value)

    val labelSp = ClockLayout.LABEL_SP
    val labelPx = labelSp * context.resources.displayMetrics.scaledDensity

    // UX-2/UX-3: tinggi baris penanda dipesan *selalu*, juga saat OnTime.
    // Kalau slot ini hanya muncul saat Delayed, digit jam akan mengecil/bergeser
    // tepat ketika penanda muncul — persis yang dilarang UX-3. Aritmetikanya
    // ada di ClockLayout (tanpa parameter status) dan dikunci ClockLayoutTest.
    val markerSp = ClockLayout.MARKER_SP
    val markerPx = markerSp * context.resources.displayMetrics.scaledDensity
    val markerGapDp = ClockLayout.MARKER_GAP_DP
    val markerSlotHdp = ClockLayout.markerSlotHeightDp

    val matrixHdp = ClockLayout.matrixHeightDp(availHdp)

    val onColor = WidgetForeground.getColor(context).toArgb()
    val offColor = WidgetInactiveDot.getColor(context).toArgb()
    val labelColor = WidgetSecondary.getColor(context).toArgb()
    // UX-2: indikator memakai warna aksen (GAY-3/GAY-4), label waktunya sekunder.
    val markerAccentColor = AccentColorProvider().getColor(context).toArgb()

    val matrix = remember(glyphs, availWdp, matrixHdp, onColor, offColor) {
        DotRender.dotMatrix(
            glyphs = glyphs,
            maxWidthPx = (availWdp * density).toInt().coerceAtLeast(32),
            maxHeightPx = (matrixHdp * density).toInt().coerceAtLeast(32),
            onColor = onColor,
            offColor = offColor,
        )
    }

    val dateText = WidgetClock.today().format(DateTimeFormatter.ofPattern("EEE, d MMM"))
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

    val delayed = freshness as? ClockFreshness.Delayed
    val markerLabel = delayed?.let { ClockFreshnessPolicy.markerLabel() }
    val markerTime = delayed?.let { ClockFreshnessPolicy.formatLastUpdate(it.lastUpdate.minuteOfDay) }
    // UX-5: kalimat lengkap dibawa gambar pertama, gambar kedua dekoratif (null)
    // supaya pembaca layar tidak mengumumkan status dua kali.
    val markerDescription = delayed?.let {
        ClockFreshnessPolicy.markerContentDescription(it.lastUpdate.minuteOfDay)
    }
    val markerLabelBitmap = remember(markerLabel, markerPx, markerAccentColor) {
        markerLabel?.let {
            DotRender.text(
                text = it,
                typeface = DotRender.typeface(context),
                textSizePx = markerPx,
                color = markerAccentColor,
                letterSpacingEm = 0.12f,
            )
        }
    }
    val markerTimeBitmap = remember(markerTime, markerPx, labelColor) {
        markerTime?.let {
            DotRender.text(
                text = it,
                typeface = DotRender.typeface(context),
                textSizePx = markerPx,
                color = labelColor,
                letterSpacingEm = 0.12f,
            )
        }
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

            Spacer(modifier = GlanceModifier.height(markerGapDp.dp))

            // STATUS-MAP §4a (UX-2, UX-3, UX-5, UX-6): slot penanda berukuran
            // tetap — kosong saat OnTime — sehingga kotak digit jam tidak
            // bergerak ketika status berubah. Terbaca tanpa interaksi pengguna.
            Box(
                modifier = GlanceModifier
                    .fillMaxWidth()
                    .height(markerSlotHdp.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (markerLabelBitmap != null && markerTimeBitmap != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Image(
                            provider = ImageProvider(markerLabelBitmap),
                            contentDescription = markerDescription,
                            contentScale = ContentScale.Fit,
                            modifier = GlanceModifier
                                .width((markerLabelBitmap.width / density).dp)
                                .height((markerLabelBitmap.height / density).dp),
                        )
                        Spacer(modifier = GlanceModifier.width(4.dp))
                        Image(
                            provider = ImageProvider(markerTimeBitmap),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = GlanceModifier
                                .width((markerTimeBitmap.width / density).dp)
                                .height((markerTimeBitmap.height / density).dp),
                        )
                    }
                }
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

    /** Alarm yang benar-benar dipasang; dipakai untuk menguji cabang izin (AC-2). */
    enum class AlarmMode { EXACT, INEXACT }

    /** Milidetik menuju batas menit berikutnya. Tepat di batas menit -> 60000. */
    fun millisToNextMinute(nowMillis: Long = System.currentTimeMillis()): Long {
        val minuteMs = 60_000L
        return minuteMs - (nowMillis % minuteMs)
    }

    /**
     * STATUS-MAP §4a (AC-2): keputusan cabang izin exact alarm.
     *
     * Sebelum Android 12 (S) izin ini belum ada, jadi exact selalu boleh —
     * `canScheduleExactAlarms()` tidak dipanggil di sana. Murni (tanpa tipe
     * Android) supaya cabang "izin ditolak" bisa diuji di JVM.
     */
    fun alarmMode(sdkInt: Int, canScheduleExactAlarms: Boolean): AlarmMode =
        if (sdkInt < Build.VERSION_CODES.S || canScheduleExactAlarms) {
            AlarmMode.EXACT
        } else {
            AlarmMode.INEXACT
        }

    /**
     * Pasang alarm sesuai [mode] dan laporkan mode yang akhirnya dipakai.
     *
     * Bila izin dicabut *di antara* pemeriksaan dan pemanggilan (balapan dengan
     * pengguna mencabut izin), `setExactAndAllowWhileIdle` melempar
     * `SecurityException`; widget tidak boleh berhenti menjadwalkan, jadi alarm
     * inexact tetap dipasang.
     */
    fun schedule(mode: AlarmMode, setExact: () -> Unit, setInexact: () -> Unit): AlarmMode {
        if (mode == AlarmMode.EXACT) {
            try {
                setExact()
                return AlarmMode.EXACT
            } catch (_: SecurityException) {
                // jatuh ke alarm inexact di bawah
            }
        }
        setInexact()
        return AlarmMode.INEXACT
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
        val canScheduleExact =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && am.canScheduleExactAlarms()
        // Izin exact alarm dicabut (Android 12+): jangan berhenti menjadwalkan —
        // alarm inexact tetap menjaga jam berganti menit, hanya bisa meleset
        // beberapa detik. Keterlambatan itulah yang ditandai UI (UX-1).
        schedule(
            mode = alarmMode(Build.VERSION.SDK_INT, canScheduleExact),
            setExact = {
                am.setExactAndAllowWhileIdle(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi
                )
            },
            setInexact = {
                am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAt, pi)
            },
        )
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
