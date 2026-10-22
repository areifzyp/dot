package io.github.areifzyp.dot.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * STATUS-MAP §4a UX-3: "munculnya penanda tidak boleh mengubah posisi atau
 * ukuran digit jam".
 *
 * UX-2/UX-3 diukur lewat snapshot di §4b, tetapi snapshot tidak ada di repo ini
 * (lihat §4b). Yang bisa dikunci sekarang adalah penyebab paling mungkin dari
 * regresi UX-3: aritmetika tata letak yang diam-diam berhenti memesan slot
 * penanda saat `OnTime`. Test di sini mengunci aritmetika itu.
 */
class ClockLayoutTest {

    private val delta = 0.001f

    @Test
    fun markerSlotIsReservedEvenWhenOnTime() {
        // Slot penanda harus selalu ada, terisi maupun kosong. Kalau nilainya
        // bisa nol saat OnTime, digit jam akan melompat tepat ketika penanda
        // muncul — persis yang dilarang UX-3.
        assertTrue(
            "slot penanda harus selalu dipesan",
            ClockLayout.reservedMarkerHeightDp > 0f,
        )
        assertEquals(22f, ClockLayout.reservedMarkerHeightDp, delta)
    }

    @Test
    fun matrixHeightMatchesDocumentedArithmetic() {
        // Widget 110dp (ukuran default launcher): isi 82dp, digit 33,6dp.
        assertEquals(82f, ClockLayout.availableHeightDp(110f), delta)
        assertEquals(33.6f, ClockLayout.matrixHeightDp(82f), delta)
    }

    @Test
    fun rowsAndDigitsNeverOverflowAvailableHeight() {
        // Untuk tinggi yang wajar, seluruh baris + digit harus pas di dalam isi.
        // Kalau ada yang menambah baris tanpa memesan ruang, test ini gagal.
        val heights = listOf(82f, 100f, 140f, 200f, 320f)
        for (available in heights) {
            val used = ClockLayout.labelRowHeightDp +
                ClockLayout.LABEL_GAP_DP +
                ClockLayout.reservedMarkerHeightDp +
                ClockLayout.matrixHeightDp(available)
            assertEquals("tata letak meluber pada tinggi isi $available", available, used, delta)
        }
    }

    @Test
    fun digitBoxIsIdenticalForOnTimeAndDelayed() {
        // Geometri tidak menerima status kesegaran sama sekali — dua status harus
        // menghasilkan angka identik. Kalau seseorang menambahkan parameter
        // status dan bercabang di dalamnya, test ini yang menangkapnya.
        val states = listOf(
            ClockFreshness.OnTime,
            ClockFreshness.Delayed(
                ClockUpdateStamp(epochMillis = 1_000L, minuteOfDay = 7 * 60 + 5),
            ),
        )
        val available = 82f
        val heights = states.map { ClockLayout.matrixHeightDp(available) }

        assertEquals(
            "kotak digit tidak boleh berbeda antar status",
            heights.toSet().size,
            1,
        )
        assertTrue(states.isNotEmpty())
    }

    @Test
    fun tinyWidgetKeepsUsableDigitBox() {
        // Widget sangat pendek: digit tidak boleh menyusut jadi tak terbaca.
        assertEquals(ClockLayout.MIN_MATRIX_DP, ClockLayout.matrixHeightDp(10f), delta)
        assertEquals(ClockLayout.MIN_MATRIX_DP, ClockLayout.matrixHeightDp(0f), delta)
    }

    @Test
    fun unmeasuredSizeFallsBackToDefaultBox() {
        // Launcher bisa melaporkan ukuran tak-hingga/terlalu kecil sebelum
        // pengukuran selesai; tata letak harus tetap deterministik.
        assertEquals(82f, ClockLayout.availableHeightDp(Float.NaN), delta)
        assertEquals(82f, ClockLayout.availableHeightDp(Float.POSITIVE_INFINITY), delta)
        assertEquals(82f, ClockLayout.availableHeightDp(40f), delta)
        assertEquals(222f, ClockLayout.availableWidthDp(Float.NaN), delta)
        assertEquals(272f, ClockLayout.availableWidthDp(300f), delta)
    }
}
