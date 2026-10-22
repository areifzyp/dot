package io.github.areifzyp.dot.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.test.core.app.ApplicationProvider
import androidx.core.content.res.ResourcesCompat
import io.github.areifzyp.dot.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File

/**
 * Verifikasi nyata (bukan mock) bahwa renderer bitmap widget menghasilkan piksel.
 * Robolectric GraphicsMode.NATIVE memakai Canvas/Skia asli, sehingga PNG yang
 * dihasilkan bisa diperiksa mata manusia.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class DotRenderTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val outDir = File("build/reports/dot-render").apply { mkdirs() }

    @Test
    fun `font Doto benar-benar termuat dari res font`() {
        val doto = ResourcesCompat.getFont(context, R.font.doto)
        assertNotNull("res/font/doto.ttf harus bisa dimuat", doto)
        assertTrue("Doto harus berbeda dari fallback monospace", doto != android.graphics.Typeface.MONOSPACE)
        assertEquals("Typeface widget harus Doto secara default", doto, DotRender.typeface(context))
    }

    @Test
    fun `dot matrix jam menghasilkan piksel menyala`() {
        val glyphs = DotMatrix.timeToGlyphs(12, 34, use24Hour = true)
        val bitmap = DotRender.dotMatrix(
            glyphs = glyphs,
            maxWidthPx = 660,
            maxHeightPx = 300,
            onColor = Color.WHITE,
            offColor = Color.DKGRAY,
        )
        save(bitmap, "clock-1234.png")

        assertTrue("bitmap harus punya lebar", bitmap.width > 100)
        assertTrue("bitmap harus punya tinggi", bitmap.height > 100)

        var bright = 0
        var dark = 0
        for (y in 0 until bitmap.height step 2) {
            for (x in 0 until bitmap.width step 2) {
                val p = bitmap.getPixel(x, y)
                if (p == Color.WHITE) bright++ else if (p == Color.DKGRAY) dark++
            }
        }
        assertTrue("harus ada titik menyala, dapat=$bright", bright > 200)
        assertTrue("harus ada titik mati (latar dot-matrix), dapat=$dark", dark > 200)
    }

    @Test
    fun `label tanggal tergambar dengan Doto`() {
        val bmp = DotRender.text(
            text = "RAB, 22 OKT",
            typeface = DotRender.typeface(context),
            textSizePx = 36f,
            color = Color.WHITE,
            letterSpacingEm = 0.14f,
        )
        save(bmp, "label-date.png")
        var painted = 0
        for (y in 0 until bmp.height) {
            for (x in 0 until bmp.width) {
                if (bmp.getPixel(x, y) != 0) painted++
            }
        }
        assertTrue("teks harus punya piksel tergambar, dapat=$painted", painted > 300)
    }

    @Test
    fun `semua digit 0-9 dan titik dua punya pola 5x7`() {
        val patterns: List<List<Int>> =
            ('0'..'9').map { digit -> DotMatrix.DIGITS.getValue(digit) } + listOf(DotMatrix.COLON)
        patterns.forEach { p ->
            assertEquals("pola harus 7 baris", DotMatrix.ROWS, p.size)
            val litCount = (0 until DotMatrix.colCount(p)).sumOf { c ->
                (0 until DotMatrix.ROWS).count { r -> DotMatrix.isDotOn(p, c, r) }
            }
            assertTrue("setiap pola harus punya titik menyala", litCount > 0)
        }
    }

    @Test
    fun `pola dekoratif mengisi seluruh area widget`() {
        val grid = io.github.areifzyp.dot.decorative.DecorPattern.FRAME.grid()
        val w = 400
        val h = 200
        val bmp = DotRender.patternGrid(grid, w, h, Color.WHITE, Color.DKGRAY)
        save(bmp, "decor-frame.png")
        assertEquals(w, bmp.width)
        assertEquals(h, bmp.height)

        // Setiap kuadran harus tersentuh titik -> pola memenuhi area, bukan sudut saja
        val quads = Array(4) { 0 }
        for (y in 0 until h step 2) {
            for (x in 0 until w step 2) {
                if (bmp.getPixel(x, y) != 0) {
                    val q = (if (y < h / 2) 0 else 2) + (if (x < w / 2) 0 else 1)
                    quads[q]++
                }
            }
        }
        quads.forEachIndexed { i, n ->
            assertTrue("kuadran $i harus berisi titik, dapat=$n", n > 0)
        }
    }

    private fun save(bitmap: Bitmap, name: String) {
        File(outDir, name).outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }
}
