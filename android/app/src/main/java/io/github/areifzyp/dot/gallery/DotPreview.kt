package io.github.areifzyp.dot.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.areifzyp.dot.widget.DotMatrix

/**
 * Pratinjau statis dot-matrix untuk Galeri (PRD §6.1 GAL-2).
 * Versi Compose dari renderer widget — sengaja dipisah karena Glance
 * (RemoteViews) dan Compose bebas tidak bisa berbagi implementasi.
 */

data class DotPalette(
    val background: Color,
    val foreground: Color,
    val inactive: Color,
    val secondary: Color,
)

/** Palet diekspos untuk pemakaian lintas file (kartu katalog). */
object PreviewPalettes {
    val light = DotPalette(
        background = Color(0xFFFFFFFF),
        foreground = Color(0xFF000000),
        inactive = Color(0xFFCCCCCC),
        secondary = Color(0xFF666666),
    )

    val dark = DotPalette(
        background = Color(0xFF000000),
        foreground = Color(0xFFFFFFFF),
        inactive = Color(0xFF444444),
        secondary = Color(0xFF999999),
    )
}

/** Dimensi dot per ukuran widget (PRD §8.2: dot 6dp, gap 2dp pada ukuran besar). */
private fun dotMetrics(size: SizeVariant): Pair<Dp, Dp> = when (size) {
    SizeVariant.SMALL -> 4.dp to 1.dp
    SizeVariant.MEDIUM -> 5.dp to 2.dp
    SizeVariant.LARGE -> 6.dp to 2.dp
}

@Composable
fun DotMatrixTimePreview(
    size: SizeVariant,
    palette: DotPalette,
    modifier: Modifier = Modifier,
    hour: Int = 9,
    minute: Int = 45,
    showDate: Boolean = size != SizeVariant.SMALL,
) {
    val (dot, gap) = dotMetrics(size)
    val glyphs = DotMatrix.timeToGlyphs(hour, minute, use24Hour = true)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(palette.background)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            glyphs.forEachIndexed { index, (_, pattern) ->
                GlyphGrid(pattern = pattern, dot = dot, gap = gap, palette = palette)
                if (index < glyphs.lastIndex) Spacer(Modifier.width(gap * 2))
            }
        }
        if (showDate) {
            Spacer(Modifier.size(dot * 1.5f))
            Text(
                text = "SAB, 24 OKT",
                color = palette.secondary,
                fontSize = if (size == SizeVariant.LARGE) 11.sp else 9.sp,
            )
        }
    }
}

@Composable
private fun GlyphGrid(
    pattern: List<Int>,
    dot: Dp,
    gap: Dp,
    palette: DotPalette,
) {
    val cols = DotMatrix.colCount(pattern)
    Column(verticalArrangement = Arrangement.spacedBy(gap)) {
        for (row in 0 until DotMatrix.ROWS) {
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                for (col in 0 until cols) {
                    val on = DotMatrix.isDotOn(pattern, col, row)
                    val isColon = cols == 1
                    val color = when {
                        on -> palette.foreground
                        isColon -> palette.background
                        else -> palette.inactive
                    }
                    Spacer(Modifier.size(dot).background(color))
                }
            }
        }
    }
}

@Composable
fun KindPreview(
    kind: WidgetKind,
    size: SizeVariant,
    palette: DotPalette,
    modifier: Modifier = Modifier,
) {
    when (kind) {
        WidgetKind.CLOCK -> DotMatrixTimePreview(size = size, palette = palette, modifier = modifier)
        WidgetKind.TEXT -> TextPreview(size = size, palette = palette, modifier = modifier)
        WidgetKind.WEATHER -> WeatherPreview(size = size, palette = palette, modifier = modifier)
        WidgetKind.CALENDAR -> CalendarPreview(size = size, palette = palette, modifier = modifier)
        WidgetKind.DEVICE -> DevicePreview(size = size, palette = palette, modifier = modifier)
        WidgetKind.MUSIC -> MusicPreview(size = size, palette = palette, modifier = modifier)
        WidgetKind.DECORATIVE -> DecorativePreview(size = size, palette = palette, modifier = modifier)
    }
}

@Composable
fun TextPreview(size: SizeVariant, palette: DotPalette, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(palette.background)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Dot.", color = palette.foreground, fontSize = 18.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("Catatan Harian", color = palette.secondary, fontSize = 11.sp)
    }
}

@Composable
fun WeatherPreview(size: SizeVariant, palette: DotPalette, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(palette.background)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Jakarta", color = palette.secondary, fontSize = 10.sp)
        Text("28°C", color = palette.foreground, fontSize = 22.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Text("Cerah Berawan", color = palette.secondary, fontSize = 10.sp)
    }
}

@Composable
fun CalendarPreview(size: SizeVariant, palette: DotPalette, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(palette.background)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("SABTU", color = palette.foreground, fontSize = 14.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Text("24 OKTOBER", color = palette.secondary, fontSize = 11.sp)
    }
}

@Composable
fun DevicePreview(size: SizeVariant, palette: DotPalette, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(palette.background)
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Baterai 85% ⚡", color = palette.foreground, fontSize = 11.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("RAM 4.2 / 8 GB", color = palette.secondary, fontSize = 10.sp)
    }
}

@Composable
fun MusicPreview(size: SizeVariant, palette: DotPalette, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(palette.background)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Now Playing", color = palette.secondary, fontSize = 10.sp)
        Text("Nothing Track", color = palette.foreground, fontSize = 12.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Text("⏮  ▶  ⏭", color = palette.foreground, fontSize = 12.sp)
    }
}

@Composable
fun DecorativePreview(size: SizeVariant, palette: DotPalette, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(palette.background)
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("• • • • •", color = palette.foreground, fontSize = 16.sp)
        Text("• • • • •", color = palette.secondary, fontSize = 16.sp)
    }
}

/**
 * Kartu pratinjau terang + gelap berdampingan (GAL-2: kedua tema, per ukuran).
 */
@Composable
fun PreviewCard(entry: WidgetEntry, size: SizeVariant, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier,
        ) {
            KindPreview(kind = entry.kind, size = size, palette = PreviewPalettes.light, modifier = Modifier.weight(1f))
            KindPreview(kind = entry.kind, size = size, palette = PreviewPalettes.dark, modifier = Modifier.weight(1f))
        }
        Text(
            text = entry.displayName(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun WidgetEntry.displayName(): String = androidx.compose.ui.res.stringResource(nameRes)