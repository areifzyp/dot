package io.github.areifzyp.dot.decorative

import android.appwidget.AppWidgetManager
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.LocalSize
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import io.github.areifzyp.dot.ui.theme.DotBlack
import io.github.areifzyp.dot.ui.theme.DotDarkInactiveDot
import io.github.areifzyp.dot.ui.theme.DotLightInactiveDot
import io.github.areifzyp.dot.ui.theme.DotWhite
import io.github.areifzyp.dot.widget.DayNightColor
import io.github.areifzyp.dot.widget.DotRender
import io.github.areifzyp.dot.widget.appWidgetIdOrNull

/**
 * Widget Dekoratif — PRD §6.7 (DEK-1..4).
 *
 * Pola titik statis mengisi penuh area widget (DEK-3). Ketukan membuka layar
 * konfigurasi untuk mengganti pola kapan pun (kriteria penerimaan §6.7).
 * DEK-4: tidak ada animasi — seluruh render murni dari preset tetap.
 *
 * Pola digambar sebagai SATU bitmap (bukan 32 Spacer berukuran tetap) supaya
 * benar-benar memenuhi area widget pada ukuran apa pun; 32 Spacer 8dp hanya
 * menutupi sudut kiri-atas dan melanggar DEK-3.
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

@Composable
private fun PatternContent(pattern: DecorPattern, onTap: androidx.glance.action.Action) {
    val context = LocalContext.current
    val density = context.resources.displayMetrics.density
    val sizeDp = LocalSize.current
    val widthDp = sizeDp.width.value.takeIf { it.isFinite() && it > 20f } ?: 180f
    val heightDp = sizeDp.height.value.takeIf { it.isFinite() && it > 20f } ?: 90f

    val active = DayNightColor(DotBlack, DotWhite).getColor(context).toArgb()
    val inactive = DayNightColor(DotLightInactiveDot, DotDarkInactiveDot).getColor(context).toArgb()

    val bitmap = remember(pattern, widthDp, heightDp, active, inactive) {
        DotRender.patternGrid(
            grid = pattern.grid(),
            widthPx = (widthDp * density).toInt().coerceAtLeast(pattern.cols),
            heightPx = (heightDp * density).toInt().coerceAtLeast(pattern.rows),
            onColor = active,
            offColor = inactive,
        )
    }

    Image(
        provider = ImageProvider(bitmap),
        contentDescription = "Pola dekoratif ${pattern.name.lowercase()}",
        contentScale = ContentScale.FillBounds,
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(16.dp)
            .clickable(onTap)
            .padding(0.dp),
    )
}
