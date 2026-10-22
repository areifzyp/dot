package io.github.areifzyp.dot.widget

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
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
 * Update tiap menit via updatePeriodMillis di clock_widget_info.xml.
 */
class ClockWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            ClockWidgetContent()
        }
    }
}

/**
 * Glance 1.1.0 tidak punya factory publik ColorProvider(day, night) —
 * hanya ColorProvider(color) dan ColorProvider(resId). Helper ini
 * mengimplementasikan interface ColorProvider dan memilih warna
 * day/night dari uiMode context widget saat render.
 */
private class DayNightColor(
    private val day: Color,
    private val night: Color,
) : ColorProvider {
    override fun getColor(context: Context): Color {
        val nightMode = context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK
        return if (nightMode == Configuration.UI_MODE_NIGHT_YES) night else day
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
private fun ClockWidgetContent() {
    val now = LocalTime.now()
    val glyphs = DotMatrix.timeToGlyphs(now.hour, now.minute)

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

            // JAM-2: baris tanggal
            val dateText = LocalDate.now().format(DateTimeFormatter.ofPattern("EEE, d MMM"))
            Text(
                text = dateText.uppercase(),
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

class ClockWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = ClockWidget()
}