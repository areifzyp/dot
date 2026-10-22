package io.github.areifzyp.dot.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import io.github.areifzyp.dot.R
import io.github.areifzyp.dot.style.DotStyleSettings

/**
 * Renderer bitmap untuk widget Glance (PRD §9 "Font kustom di widget").
 *
 * Dua alasan widget Dot memakai bitmap, bukan komposisi view:
 * 1. Glance hanya menerima `FontFamily(namaFamily)` berupa nama font sistem —
 *    tidak ada API untuk `res/font`. Satu-satunya cara memakai Doto di widget
 *    adalah menggambar teks ke Bitmap lalu menampilkannya sebagai Image.
 * 2. Dot-matrix 5x7 sebagai Spacer berarti ~147 view per widget; RemoteViews
 *    harus di-inflate launcher dan sering gagal/blank. Satu Bitmap = satu view.
 *
 * Semua ukuran dalam piksel fisik; pemanggil mengalikan dp dengan density.
 */
object DotRender {

    /** Rasio celah antar titik terhadap diameter titik (gaya Nothing: rapat). */
    private const val GAP_RATIO = 0.34f

    /** Rasio celah antar glyph (digit) terhadap satu satuan sel. */
    private const val GLYPH_GAP_RATIO = 1.1f

    /** Diameter titik efektif relatif terhadap sel. */
    private const val DOT_FILL = 0.96f

    private var cachedDoto: Typeface? = null

    /** Typeface untuk widget: Doto bila dipilih pengguna, selain itu sans sistem. */
    fun typeface(context: Context): Typeface {
        if (DotStyleSettings.getFont(context) == DotStyleSettings.FontChoice.SYSTEM_SANS) {
            return Typeface.SANS_SERIF
        }
        cachedDoto?.let { return it }
        val resolved = runCatching { ResourcesCompat.getFont(context, R.font.doto) }
            .getOrNull()
            ?: Typeface.MONOSPACE
        cachedDoto = resolved
        return resolved
    }

    /**
     * Gambar jam "HH:MM" sebagai dot-matrix ke Bitmap.
     *
     * @param maxWidthPx  lebar maksimum yang tersedia (px fisik)
     * @param maxHeightPx tinggi maksimum yang tersedia (px fisik)
     * @param onColor     warna titik aktif (ARGB)
     * @param offColor    warna titik non-aktif; null = transparan
     */
    fun dotMatrix(
        glyphs: List<Pair<String, List<Int>>>,
        maxWidthPx: Int,
        maxHeightPx: Int,
        onColor: Int,
        offColor: Int?,
    ): Bitmap {
        require(glyphs.isNotEmpty()) { "glyphs kosong" }
        val colsPerGlyph = glyphs.map { DotMatrix.colCount(it.second) }
        val gapUnits = GAP_RATIO
        val glyphGapUnits = GLYPH_GAP_RATIO

        // Lebar/tinggi dalam satuan "sel" (1 sel = 1 titik + celahnya)
        val widthUnits = colsPerGlyph.sumOf { (it + (it - 1) * gapUnits).toDouble() } +
            (glyphs.size - 1) * glyphGapUnits
        val heightUnits = DotMatrix.ROWS + (DotMatrix.ROWS - 1) * gapUnits

        val safeW = maxWidthPx.coerceAtLeast(24)
        val safeH = maxHeightPx.coerceAtLeast(24)
        val cellByWidth = safeW / widthUnits.toFloat()
        val cellByHeight = safeH / heightUnits.toFloat()
        val cell = minOf(cellByWidth, cellByHeight).coerceAtLeast(2f)
        val gap = cell * gapUnits
        val glyphGap = cell * glyphGapUnits
        val radius = cell * DOT_FILL / 2f

        val bmpW = ((widthUnits * cell).toInt() + 1).coerceAtLeast(1)
        val bmpH = ((heightUnits * cell).toInt() + 1).coerceAtLeast(1)
        val bitmap = Bitmap.createBitmap(bmpW, bmpH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }

        var x = 0f
        glyphs.forEachIndexed { gIndex, (_, pattern) ->
            val cols = DotMatrix.colCount(pattern)
            for (row in 0 until DotMatrix.ROWS) {
                val cy = row * (cell + gap) + cell / 2f
                for (col in 0 until cols) {
                    val isOn = DotMatrix.isDotOn(pattern, col, row)
                    val color = if (isOn) onColor else offColor ?: continue
                    paint.color = color
                    val cx = x + col * (cell + gap) + cell / 2f
                    canvas.drawCircle(cx, cy, radius, paint)
                }
            }
            x += cols * cell + (cols - 1) * gap
            if (gIndex < glyphs.lastIndex) x += glyphGap
        }
        return bitmap
    }

    /**
     * Render pola dekoratif (grid boolean) memenuhi seluruh area widget (DEK-3).
     *
     * Titik tetap bulat; pusat tiap sel disebar merata pada lebar/tinggi penuh
     * sehingga pola mengisi area tanpa menyisakan margin besar di satu sisi.
     */
    fun patternGrid(
        grid: List<BooleanArray>,
        widthPx: Int,
        heightPx: Int,
        onColor: Int,
        offColor: Int,
    ): Bitmap {
        require(grid.isNotEmpty() && grid.first().isNotEmpty()) { "grid kosong" }
        val rows = grid.size
        val cols = grid.first().size
        val w = widthPx.coerceAtLeast(cols)
        val h = heightPx.coerceAtLeast(rows)
        val cellW = w.toFloat() / cols
        val cellH = h.toFloat() / rows
        val radius = minOf(cellW, cellH) * 0.36f

        val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                paint.color = if (grid[r][c]) onColor else offColor
                canvas.drawCircle((c + 0.5f) * cellW, (r + 0.5f) * cellH, radius, paint)
            }
        }
        return bitmap
    }

    /**
     * Render satu baris teks memakai typeface Dot (biasanya Doto).
     * Bitmap dikembalikan dengan latar transparan.
     */
    fun text(
        text: String,
        typeface: Typeface,
        textSizePx: Float,
        color: Int,
        letterSpacingEm: Float = 0.06f,
        maxWidthPx: Int? = null,
    ): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.typeface = typeface
            this.textSize = textSizePx
            this.color = color
            this.letterSpacing = letterSpacingEm
        }
        val metrics = paint.fontMetrics
        val width = paint.measureText(text).toInt().coerceAtLeast(1)
        val height = (metrics.descent - metrics.ascent).toInt().coerceAtLeast(1)
        val pad = (textSizePx * 0.18f).toInt()

        val bmpW = (maxWidthPx ?: width).coerceAtLeast(width) + pad * 2
        val bmpH = height + pad * 2
        val bitmap = Bitmap.createBitmap(bmpW, bmpH, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        // Baseline: padding atas + jarak ascent
        canvas.drawText(text, pad.toFloat(), pad - metrics.ascent, paint)
        return bitmap
    }
}
