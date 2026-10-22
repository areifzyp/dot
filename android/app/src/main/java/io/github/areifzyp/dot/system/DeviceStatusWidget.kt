package io.github.areifzyp.dot.system

import android.content.Context
import android.content.Intent
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.background
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.updateAll
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

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
import androidx.glance.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.areifzyp.dot.MainActivity
import io.github.areifzyp.dot.widget.WidgetIntents
import androidx.glance.LocalContext
import io.github.areifzyp.dot.ui.theme.DotBlack
import io.github.areifzyp.dot.ui.theme.DotDarkInactiveDot
import io.github.areifzyp.dot.ui.theme.DotDarkSecondary
import io.github.areifzyp.dot.ui.theme.DotGraySecondaryLight
import io.github.areifzyp.dot.ui.theme.DotLightInactiveDot
import io.github.areifzyp.dot.ui.theme.DotWhite
import io.github.areifzyp.dot.widget.DayNightColor

/**
 * Widget Status HP — PRD §6.5 (HP-1..HP-5).
 *
 * Metrik dibaca segar setiap render (nilai nyata, bukan cache) sehingga
 * perubahan baterai signifikan langsung tampak saat receiver dipicu broadcast.
 */
class DeviceStatusWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val stats = SystemStatsReader.readAll(context, CpuSampler())
        provideContent { DeviceStatusContent(stats) }
    }
}

class DeviceStatusWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DeviceStatusWidget()

    override fun onReceive(context: Context, intent: Intent) {
        // HP-5: pasang widget → aktifkan penyegaran terjadwal 15 menit (idempoten).
        if (intent.action == "android.appwidget.action.APPWIDGET_ENABLED" ||
            intent.action == "android.appwidget.action.APPWIDGET_UPDATE"
        ) {
            DeviceStatsWorker.schedule(context)
        }
        if (intent.action == "android.appwidget.action.APPWIDGET_DISABLED") {
            DeviceStatsWorker.cancelIfNoWidgets(context)
        }
        // HP-5: perubahan baterai signifikan → update segera, jangan tunggu 15 menit.
        if (Intent.ACTION_BATTERY_CHANGED == intent.action ||
            Intent.ACTION_POWER_CONNECTED == intent.action ||
            Intent.ACTION_POWER_DISCONNECTED == intent.action
        ) {
            val app = context.applicationContext
            val widget = glanceAppWidget
            CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
                try {
                    widget.updateAll(app)
                } catch (_: Exception) {
                    // kegagalan update sementara diabaikan; jadwal 15 menit masih berlaku
                }
            }
        }
        super.onReceive(context, intent)
    }
}

private const val BAR_DOTS = 10

@androidx.compose.runtime.Composable
private fun DeviceStatusContent(stats: SystemStats) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(12.dp)
            .clickable(actionStartActivity(WidgetIntents.battery(LocalContext.current))),
    ) {
        MetricRow(
            label = "Baterai",
            metric = stats.battery,
            suffix = when (stats.charging) {
                ChargingState.CHARGING -> " ⚡"
                ChargingState.FULL -> " •"
                else -> ""
            },
        )
        Spacer(GlanceModifier.height(6.dp))
        MetricRow(label = "RAM", metric = stats.ram)
        Spacer(GlanceModifier.height(6.dp))
        MetricRow(label = "Penyimpanan", metric = stats.storage)
        Spacer(GlanceModifier.height(6.dp))
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = GlanceModifier.width(84.dp)) {
                MetricRow(label = "CPU", metric = stats.cpu)
            }
            Spacer(GlanceModifier.width(8.dp))
            val temp = stats.temperatureC
            Text(
                text = if (temp != null) "%.1f°C".format(temp) else "—",
                style = TextStyle(
                    color = DayNightColor(DotGraySecondaryLight, DotDarkSecondary),
                    fontSize = 11.sp,
                ),
            )
        }
    }
}

@androidx.compose.runtime.Composable
private fun MetricRow(label: String, metric: Metric, suffix: String = "") {
    Row(
        modifier = GlanceModifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            modifier = GlanceModifier.width(84.dp),
            style = TextStyle(
                color = DayNightColor(DotGraySecondaryLight, DotDarkSecondary),
                fontSize = 11.sp,
            ),
        )
        // HP-4: bar dot-matrix proporsional (60% = 6 dari 10 titik)
        when (metric) {
            is Metric.Unavailable -> Text(
                text = "tidak tersedia",
                style = TextStyle(
                    color = DayNightColor(DotGraySecondaryLight, DotDarkSecondary),
                    fontSize = 11.sp,
                ),
            )
            is Metric.Percent -> {
                DotBar(filled = dotsFor(metric.value))
                Spacer(GlanceModifier.width(6.dp))
                Text(
                    text = "${metric.value}%$suffix",
                    style = TextStyle(
                        color = DayNightColor(DotBlack, DotWhite),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                    ),
                )
            }
        }
    }
}

/** Jumlah titik menyala dari persen (pembulatan ke bawah, minimum 1 bila >0). */
internal fun dotsFor(percent: Int, total: Int = BAR_DOTS): Int = when {
    percent <= 0 -> 0
    percent >= 100 -> total
    else -> (percent * total / 100).coerceAtLeast(1)
}

@androidx.compose.runtime.Composable
private fun DotBar(filled: Int, total: Int = BAR_DOTS) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        repeat(total) { index ->
            val color = if (index < filled) {
                DayNightColor(DotBlack, DotWhite)
            } else {
                DayNightColor(DotLightInactiveDot, DotDarkInactiveDot)
            }
            Spacer(
                GlanceModifier
                    .padding(1.dp)
                    .width(7.dp)
                    .height(7.dp)
                    .background(color),
            )
        }
    }
}