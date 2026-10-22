package io.github.areifzyp.dot.decorative

/**
 * Preset pola titik statis untuk Widget Dekoratif (PRD §6.7 DEK-1).
 *
 * Grid dinyatakan sebagai fungsi (row, col) → Boolean pada dimensi [rows]×[cols],
 * sehingga unit test dapat memverifikasi dimensi dan jumlah titik menyala
 * tanpa bergantung pada render Glance.
 *
 * DEK-4: seluruh pola statis (fungsi murni, tanpa animasi/state waktu).
 */
enum class DecorPattern(val rows: Int, val cols: Int) {
    /** Titik penuh — semua sel menyala. */
    FULL(4, 8),

    /** Garis horizontal — baris tengah menyala. */
    HORIZONTAL_LINE(4, 8),

    /** Garis vertikal — kolom tengah menyala. */
    VERTICAL_LINE(4, 8),

    /** Diagonal dari kiri-atas ke kanan-bawah. */
    DIAGONAL(4, 8),

    /** Kotak/bingkai — hanya sel tepi. */
    FRAME(4, 8),

    /** Silang — diagonal utama + anti-diagonal. */
    CROSS(4, 8),

    /** Acak deterministik (seed tetap) — reproducer antar render. */
    RANDOM(4, 8),

    /** Lingkaran titik (approximasi raster lingkaran di tengah grid). */
    CIRCLE(4, 8),
    ;

    /** @return true bila sel (r,c) menyala pada pola ini. */
    fun lit(r: Int, c: Int): Boolean {
        require(r in 0 until rows && c in 0 until cols) { "sel ($r,$c) di luar grid $rows×$cols" }
        val cr = (rows - 1) / 2f
        val cc = (cols - 1) / 2f
        return when (this) {
            FULL -> true
            HORIZONTAL_LINE -> r == rows / 2 || r == rows / 2 - 1 && rows % 2 == 0
            VERTICAL_LINE -> c == cols / 2 || c == cols / 2 - 1 && cols % 2 == 0
            DIAGONAL -> {
                // proyeksi diagonal: r/rows ≈ c/cols
                kotlin.math.abs(r.toFloat() / rows - c.toFloat() / cols) < 1f / cols
            }
            FRAME -> r == 0 || r == rows - 1 || c == 0 || c == cols - 1
            CROSS -> {
                val d1 = kotlin.math.abs(r.toFloat() / rows - c.toFloat() / cols)
                val d2 = kotlin.math.abs(r.toFloat() / rows - (1f - c.toFloat() / cols))
                d1 < 1f / cols || d2 < 1f / cols
            }
            RANDOM -> {
                // xorshift deterministik per sel, seed tetap (DEK-1 kriteria)
                var h = (r * 31 + c * 17 + 0x5DEECE66L) and 0xFFFFFFFFFFFFFL
                h = h xor (h shl 13)
                h = h xor (h ushr 7)
                h = h xor (h shl 17)
                (h and 1L) == 1L
            }
            CIRCLE -> {
                // raster lingkaran: jarak normalisasi mendekati 1 (band tipis)
                val dr = (r - cr) / (rows / 2f)
                val dc = (c - cc) / (cols / 2f)
                val dist = kotlin.math.sqrt(dr * dr + dc * dc)
                dist <= 1f && dist >= 0.45f
            }
        }
    }

    /** Grid boolean rows×cols (dipakai unit test dan render). */
    fun grid(): List<BooleanArray> =
        List(rows) { r -> BooleanArray(cols) { c -> lit(r, c) } }

    /** Jumlah titik menyala — diverifikasi unit test per preset. */
    fun litCount(): Int = grid().sumOf { row -> row.count { it } }

    companion object {
        /** Minimal 6 preset (DEK-1). */
        const val MIN_PRESETS = 6

        /** Semua preset yang tersedia untuk konfigurasi. */
        val selectable: List<DecorPattern> = entries.toList()
    }
}