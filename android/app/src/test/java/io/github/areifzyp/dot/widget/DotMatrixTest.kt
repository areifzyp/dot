package io.github.areifzyp.dot.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DotMatrixTest {

    @Test
    fun allDigitsAreDefinedWithCorrectDimensions() {
        for (c in '0'..'9') {
            val pattern = DotMatrix.DIGITS[c] ?: error("Digit $c missing")
            assertEquals("Digit $c must have ${DotMatrix.ROWS} rows", DotMatrix.ROWS, pattern.size)
            pattern.forEachIndexed { row, bits ->
                assertTrue("Digit $c row $row exceeds ${DotMatrix.COLS} bits", bits < (1 shl DotMatrix.COLS))
            }
        }
    }

    @Test
    fun isDotOnReadsBitsLeftToRight() {
        // '1': baris 1 = 0b01100 → titik di kolom 1 dan 2
        val one = DotMatrix.DIGITS['1']!!
        assertFalse(DotMatrix.isDotOn(one, 0, 1))
        assertTrue(DotMatrix.isDotOn(one, 1, 1))
        assertTrue(DotMatrix.isDotOn(one, 2, 1))
        assertFalse(DotMatrix.isDotOn(one, 3, 1))
        assertFalse(DotMatrix.isDotOn(one, 4, 1))
    }

    @Test
    fun isDotOnHandlesOutOfBounds() {
        val zero = DotMatrix.DIGITS['0']!!
        assertFalse(DotMatrix.isDotOn(zero, -1, 0))
        assertFalse(DotMatrix.isDotOn(zero, 5, 0))
        assertFalse(DotMatrix.isDotOn(zero, 0, -1))
        assertFalse(DotMatrix.isDotOn(zero, 0, 7))
    }

    @Test
    fun colonIsOneColumnWideWithDotsAtRows1And3() {
        assertEquals(1, DotMatrix.colCount(DotMatrix.COLON))
        assertFalse(DotMatrix.isDotOn(DotMatrix.COLON, 0, 0))
        assertTrue(DotMatrix.isDotOn(DotMatrix.COLON, 0, 1))
        assertFalse(DotMatrix.isDotOn(DotMatrix.COLON, 0, 2))
        assertTrue(DotMatrix.isDotOn(DotMatrix.COLON, 0, 3))
        assertFalse(DotMatrix.isDotOn(DotMatrix.COLON, 0, 4))
    }

    @Test
    fun timeToGlyphsProducesFiveGlyphs() {
        val glyphs = DotMatrix.timeToGlyphs(7, 5)
        assertEquals(5, glyphs.size)
        assertEquals("0", glyphs[0].first)
        assertEquals("7", glyphs[1].first)
        assertEquals(":", glyphs[2].first)
        assertEquals("0", glyphs[3].first)
        assertEquals("5", glyphs[4].first)
    }

    @Test
    fun timeToGlyphsPadsDoubleDigits() {
        val glyphs = DotMatrix.timeToGlyphs(23, 59)
        assertEquals(listOf("2", "3", ":", "5", "9"), glyphs.map { it.first })
    }

    @Test
    fun to12HourDisplayConvertsCorrectly() {
        assertEquals(12, DotMatrix.to12HourDisplay(0))
        assertEquals(1, DotMatrix.to12HourDisplay(1))
        assertEquals(12, DotMatrix.to12HourDisplay(12))
        assertEquals(11, DotMatrix.to12HourDisplay(23))
        assertEquals(1, DotMatrix.to12HourDisplay(13))
    }

    @Test
    fun timeToGlyphsRespects12HourFormat() {
        // 00:05 -> 12:05 dalam format 12 jam
        val midnight = DotMatrix.timeToGlyphs(0, 5, use24Hour = false)
        assertEquals(listOf("1", "2", ":", "0", "5"), midnight.map { it.first })
        // 23:59 -> 11:59
        val late = DotMatrix.timeToGlyphs(23, 59, use24Hour = false)
        assertEquals(listOf("1", "1", ":", "5", "9"), late.map { it.first })
        // 13:00 -> 01:00 (padStart tetap berlaku)
        val afternoon = DotMatrix.timeToGlyphs(13, 0, use24Hour = false)
        assertEquals(listOf("0", "1", ":", "0", "0"), afternoon.map { it.first })
    }
}