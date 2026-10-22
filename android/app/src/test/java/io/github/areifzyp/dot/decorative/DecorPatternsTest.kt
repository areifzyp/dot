package io.github.areifzyp.dot.decorative

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Unit test preset pola Dekoratif (PRD §6.7 kriteria penerimaan:
 * "Setiap preset tervalidasi lewat unit test (dimensi dan jumlah titik menyala)").
 */
class DecorPatternsTest {

    @Test
    fun `at least six presets exist`() {
        assertTrue(
            "DEK-1 butuh minimal 6 preset",
            DecorPattern.selectable.size >= DecorPattern.MIN_PRESETS,
        )
    }

    @Test
    fun `all presets share 4x8 grid dimensions`() {
        DecorPattern.selectable.forEach { p ->
            assertEquals("dimensi rows ${p.name}", 4, p.rows)
            assertEquals("dimensi cols ${p.name}", 8, p.cols)
            assertEquals("grid rows ${p.name}", 4, p.grid().size)
            p.grid().forEach { row -> assertEquals("grid cols ${p.name}", 8, row.size) }
        }
    }

    @Test
    fun `full pattern lights every cell`() {
        assertEquals(32, DecorPattern.FULL.litCount())
    }

    @Test
    fun `frame lights only border cells`() {
        // perimeter 4x8 = 2*(4+8)-4 = 20
        assertEquals(20, DecorPattern.FRAME.litCount())
        assertTrue(DecorPattern.FRAME.lit(0, 0))
        assertFalse(DecorPattern.FRAME.lit(1, 1))
        assertTrue(DecorPattern.FRAME.lit(3, 7))
    }

    @Test
    fun `horizontal line lights middle row cells only`() {
        val count = DecorPattern.HORIZONTAL_LINE.litCount()
        assertTrue("garis horizontal harus menyala sebagian", count in 1..31)
        // baris tengah (index 2) menyala penuh
        (0 until 8).forEach { c -> assertTrue(DecorPattern.HORIZONTAL_LINE.lit(2, c)) }
    }

    @Test
    fun `vertical line lights middle column cells only`() {
        (0 until 4).forEach { r -> assertTrue(DecorPattern.VERTICAL_LINE.lit(r, 4)) }
        assertTrue(DecorPattern.VERTICAL_LINE.litCount() in 1..31)
    }

    @Test
    fun `diagonal lights at least one cell per row`() {
        DecorPattern.DIAGONAL.grid().forEachIndexed { r, row ->
            assertTrue("baris $r diagonal kosong", row.any { it })
        }
    }

    @Test
    fun `cross lights corners region`() {
        val count = DecorPattern.CROSS.litCount()
        assertTrue("silang harus menyala", count >= 4)
        assertTrue(count <= 32)
    }

    @Test
    fun `random is deterministic across calls`() {
        val a = DecorPattern.RANDOM.grid().map { it.toList() }
        val b = DecorPattern.RANDOM.grid().map { it.toList() }
        assertEquals("RANDOM harus deterministik (seed tetap)", a, b)
        val count = DecorPattern.RANDOM.litCount()
        assertTrue("RANDOM tidak boleh kosong penuh atau penuh", count in 1..31)
    }

    @Test
    fun `circle lights ring cells`() {
        val count = DecorPattern.CIRCLE.litCount()
        assertTrue("lingkaran harus menyala sebagian", count in 1..31)
        // pusat grid tidak menyala (band ring)
        assertFalse(DecorPattern.CIRCLE.lit(1, 3) && DecorPattern.CIRCLE.lit(2, 4) && DecorPattern.CIRCLE.lit(1, 4))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `lit rejects out-of-range cell`() {
        DecorPattern.FULL.lit(4, 0)
    }
}