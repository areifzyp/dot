package io.github.areifzyp.dot.decorative

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import io.github.areifzyp.dot.widget.appWidgetIdOrNull
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.background
import androidx.compose.ui.unit.dp
import io.github.areifzyp.dot.ui.theme.DotBlack
import io.github.areifzyp.dot.ui.theme.DotDarkInactiveDot
import io.github.areifzyp.dot.ui.theme.DotLightInactiveDot
import io.github.areifzyp.dot.ui.theme.DotWhite
import io.github.areifzyp.dot.widget.DayNightColor

/**
 * Widget Dekoratif — PRD §6.7 (DEK-1..4).
 *
 * Pola titik statis mengisi penuh area widget (DEK-3). Ketukan membuka layar
 * konfigurasi untuk mengganti pola kapan pun (kriteria penerimaan §6.7).
 * DEK-4: tidak ada animasi — seluruh render murni dari preset tetap.
 */
class DecorativeWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val widgetId = id.appWidgetIdOrNull()
            ?: AppWidgetManager.INVALID_APPWIDGET_ID
        val pattern = DecorSettings.load(context, widgetId)
        provideContent {
            val configIntent = android.content.Intent(
                context,
                DecorativeWidgetConfigActivity::class.java,
            ).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
            PatternContent(
                pattern = pattern,
                onTap = actionStartActivity(configIntent),
            )
        }
    }
}

class DecorativeWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DecorativeWidget()
}

@androidx.compose.runtime.Composable
private fun PatternContent(pattern: DecorPattern, onTap: androidx.glance.action.Action) {
    val active = DayNightColor(DotBlack, DotWhite)
    val inactive = DayNightColor(DotLightInactiveDot, DotDarkInactiveDot)
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(8.dp)
            .clickable(onTap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        pattern.grid().forEach { row ->
            Row {
                row.forEach { on ->
                    Spacer(
                        GlanceModifier
                            .padding(1.dp)
                            .width(8.dp)
                            .height(8.dp)
                            .background(if (on) active else inactive),
                    )
                }
            }
        }
    }
}