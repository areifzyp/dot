package io.github.areifzyp.dot.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * State machine kesegaran widget jam — STATUS-MAP §4a UX-1, UX-2, UX-4, UX-5.
 * Waktu disuntik (tidak memakai jam sistem) supaya hasilnya deterministik.
 */
class ClockFreshnessTest {

    private val baseline = ClockUpdateStamp(
        epochMillis = 1_700_000_000_000L,
        minuteOfDay = 12 * 60 + 5, // 12:05
    )

    private fun afterMinutes(minutes: Long): Long =
        baseline.epochMillis + minutes * 60_000L

    @Test
    fun zeroAndOneMinuteAreOnTime() {
        assertEquals(ClockFreshness.OnTime, ClockFreshnessPolicy.evaluate(baseline, afterMinutes(0)))
        assertEquals(ClockFreshness.OnTime, ClockFreshnessPolicy.evaluate(baseline, afterMinutes(1)))
    }

    @Test
    fun twoFiveAndSixtyMinutesAreDelayed() {
        listOf(2L, 5L, 60L).forEach { minutes ->
            val status = ClockFreshnessPolicy.evaluate(baseline, afterMinutes(minutes))
            assertTrue("offset $minutes menit harus Delayed", status is ClockFreshness.Delayed)
            assertEquals(baseline, (status as ClockFreshness.Delayed).lastUpdate)
        }
    }

    @Test
    fun justUnderTwoMinutesIsStillOnTime() {
        // Batas persis: 119_999 ms belum tertunda, 120_000 ms sudah.
        assertEquals(
            ClockFreshness.OnTime,
            ClockFreshnessPolicy.evaluate(baseline, baseline.epochMillis + 119_999L),
        )
        assertTrue(
            ClockFreshnessPolicy.evaluate(baseline, baseline.epochMillis + 120_000L)
                is ClockFreshness.Delayed
        )
    }

    @Test
    fun baselineInTheFutureIsOnTime() {
        // Jam perangkat dimundurkan: selisih negatif tidak boleh memicu penanda.
        assertEquals(
            ClockFreshness.OnTime,
            ClockFreshnessPolicy.evaluate(baseline, baseline.epochMillis - 600_000L),
        )
    }

    @Test
    fun recoversToOnTimeOnNextRender() {
        // UX-4: render terlambat -> Delayed, lalu pembaruan berikutnya tepat waktu
        // -> OnTime, tanpa state macet.
        val lateRender = ClockFreshnessPolicy.evaluate(baseline, afterMinutes(5))
        assertTrue(lateRender is ClockFreshness.Delayed)

        val caughtUp = ClockUpdateStamp(
            epochMillis = afterMinutes(5),
            minuteOfDay = ClockFreshnessPolicy.minuteOfDay(ClockFreshnessPolicy.toLocalTime(12 * 60 + 10)),
        )
        assertEquals(
            ClockFreshness.OnTime,
            ClockFreshnessPolicy.evaluate(caughtUp, caughtUp.epochMillis + 30_000L),
        )
    }

    @Test
    fun markerCarriesTimeAndReadableDescription() {
        // UX-2: label HH:MM; UX-5: status dibawa teks, bukan warna saja.
        assertEquals("12:05", ClockFreshnessPolicy.formatLastUpdate(12 * 60 + 5))
        assertEquals("TERTUNDA", ClockFreshnessPolicy.markerLabel())
        assertEquals(
            "Pembaruan tertunda, terakhir 12:05",
            ClockFreshnessPolicy.markerContentDescription(12 * 60 + 5),
        )
    }

    @Test
    fun labelIsAlwaysTwoDigitsIn24HourForm() {
        assertEquals("00:00", ClockFreshnessPolicy.formatLastUpdate(0))
        assertEquals("09:07", ClockFreshnessPolicy.formatLastUpdate(9 * 60 + 7))
        assertEquals("23:59", ClockFreshnessPolicy.formatLastUpdate(23 * 60 + 59))
    }

    @Test
    fun minuteOfDayRoundTripsAndNormalizesOutOfRange() {
        assertEquals(0, ClockFreshnessPolicy.minuteOfDay(ClockFreshnessPolicy.toLocalTime(0)))
        assertEquals(
            23 * 60 + 59,
            ClockFreshnessPolicy.minuteOfDay(ClockFreshnessPolicy.toLocalTime(23 * 60 + 59)),
        )
        // Input rusak tidak boleh melempar: diputar ke dalam sehari.
        assertEquals(0, ClockFreshnessPolicy.minuteOfDay(ClockFreshnessPolicy.toLocalTime(24 * 60)))
        assertEquals(
            23 * 60 + 59,
            ClockFreshnessPolicy.minuteOfDay(ClockFreshnessPolicy.toLocalTime(-1)),
        )
    }
}
