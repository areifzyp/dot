package io.github.areifzyp.dot.widget

/**
 * Dot-matrix glyph definitions for clock digits.
 * Each digit is a 5-column × 7-row grid (PRD §8.2, §5 JAM-1).
 * 1 = dot visible, 0 = empty.
 */
object DotMatrix {

    const val COLS = 5
    const val ROWS = 7

    // Digits 0–9 as 5×7 bitmaps (row-major, MSB = leftmost column)
    val DIGITS: Map<Char, List<Int>> = mapOf(
        '0' to listOf(
            0b01110,
            0b10001,
            0b10011,
            0b10101,
            0b11001,
            0b10001,
            0b01110,
        ),
        '1' to listOf(
            0b00100,
            0b01100,
            0b00100,
            0b00100,
            0b00100,
            0b00100,
            0b01110,
        ),
        '2' to listOf(
            0b01110,
            0b10001,
            0b00001,
            0b00110,
            0b01000,
            0b10000,
            0b11111,
        ),
        '3' to listOf(
            0b01110,
            0b10001,
            0b00001,
            0b00110,
            0b00001,
            0b10001,
            0b01110,
        ),
        '4' to listOf(
            0b00010,
            0b00110,
            0b01010,
            0b10010,
            0b11111,
            0b00010,
            0b00010,
        ),
        '5' to listOf(
            0b11111,
            0b10000,
            0b11110,
            0b00001,
            0b00001,
            0b10001,
            0b01110,
        ),
        '6' to listOf(
            0b00110,
            0b01000,
            0b10000,
            0b11110,
            0b10001,
            0b10001,
            0b01110,
        ),
        '7' to listOf(
            0b11111,
            0b00001,
            0b00010,
            0b00100,
            0b01000,
            0b01000,
            0b01000,
        ),
        '8' to listOf(
            0b01110,
            0b10001,
            0b10001,
            0b01110,
            0b10001,
            0b10001,
            0b01110,
        ),
        '9' to listOf(
            0b01110,
            0b10001,
            0b10001,
            0b01111,
            0b00001,
            0b00010,
            0b01100,
        ),
    )

    // Colon separator: 1 kolom, titik di baris 1 dan 3 (bit di posisi kolom 0 / LSB)
    val COLON: List<Int> = listOf(
        0b0,
        0b1,
        0b0,
        0b1,
        0b0,
        0b0,
        0b0,
    )

    /** Kolom efektif sebuah pola (digit = 5, colon = 1). */
    fun colCount(pattern: List<Int>): Int =
        if (pattern === COLON) 1 else COLS

    /**
     * Returns true if the dot at (col, row) should be drawn for the given glyph pattern.
     * Bit paling kiri pola berada di posisi (colCount-1); bit di luar lebar pola = 0.
     */
    fun isDotOn(pattern: List<Int>, col: Int, row: Int): Boolean {
        if (row < 0 || row >= pattern.size) return false
        val cols = colCount(pattern)
        if (col < 0 || col >= cols) return false
        val rowData = pattern[row]
        val shift = (cols - 1) - col
        return ((rowData shr shift) and 1) == 1
    }

    /** Konversi jam 0-23 ke tampilan format 12 jam (1-12). */
    fun to12HourDisplay(hour: Int): Int = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }

    /**
     * Build the full time string "HH:MM" as a list of glyph patterns.
     * [use24Hour]=false menampilkan jam dalam format 12 jam (1-12).
     */
    fun timeToGlyphs(hour: Int, minute: Int, use24Hour: Boolean = true): List<Pair<String, List<Int>>> {
        val displayHour = if (use24Hour) hour else to12HourDisplay(hour)
        val h = displayHour.toString().padStart(2, '0')
        val m = minute.toString().padStart(2, '0')
        val chars = "$h:$m"
        return chars.map { c ->
            if (c == ':') Pair(":", COLON)
            else Pair(c.toString(), DIGITS[c] ?: DIGITS['0']!!)
        }
    }
}