package io.github.areifzyp.dot.widget

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Test murni JVM (tanpa Android) untuk penjadwalan update jam.
 *
 * STATUS-MAP §4a AC-2: cabang "izin exact alarm ditolak" harus diuji. Di sini
 * yang diuji adalah keputusan ([ClockUpdateScheduler.alarmMode]) dan pemasangan
 * alarm yang dipilih ([ClockUpdateScheduler.schedule]); jalur AlarmManager
 * sungguhan diuji di [ClockExactAlarmPermissionTest] lewat Robolectric.
 */
class ClockUpdateSchedulerTest {

    @Test
    fun exactlyOnMinuteBoundarySchedulesFullMinute() {
        assertEquals(60_000L, ClockUpdateScheduler.millisToNextMinute(0L))
        assertEquals(60_000L, ClockUpdateScheduler.millisToNextMinute(120_000L))
    }

    @Test
    fun midMinuteSchedulesRemainder() {
        assertEquals(12_700L, ClockUpdateScheduler.millisToNextMinute(47_300L))
        assertEquals(1L, ClockUpdateScheduler.millisToNextMinute(59_999L))
        assertEquals(59_999L, ClockUpdateScheduler.millisToNextMinute(1L))
    }

    @Test
    fun beforeAndroid12ExactAlarmIsAlwaysAllowed() {
        // API 26 (minSdk) dan 30: izin exact alarm belum ada, canScheduleExactAlarms
        // tidak dipanggil, jadi jawabannya exact apa pun nilai boolean-nya.
        assertEquals(
            ClockUpdateScheduler.AlarmMode.EXACT,
            ClockUpdateScheduler.alarmMode(sdkInt = 26, canScheduleExactAlarms = false),
        )
        assertEquals(
            ClockUpdateScheduler.AlarmMode.EXACT,
            ClockUpdateScheduler.alarmMode(sdkInt = 30, canScheduleExactAlarms = false),
        )
    }

    @Test
    fun android12AndAboveWithPermissionUsesExactAlarm() {
        assertEquals(
            ClockUpdateScheduler.AlarmMode.EXACT,
            ClockUpdateScheduler.alarmMode(sdkInt = 31, canScheduleExactAlarms = true),
        )
        assertEquals(
            ClockUpdateScheduler.AlarmMode.EXACT,
            ClockUpdateScheduler.alarmMode(sdkInt = 34, canScheduleExactAlarms = true),
        )
    }

    @Test
    fun android12AndAboveWithoutPermissionFallsBackToInexact() {
        assertEquals(
            ClockUpdateScheduler.AlarmMode.INEXACT,
            ClockUpdateScheduler.alarmMode(sdkInt = 31, canScheduleExactAlarms = false),
        )
        assertEquals(
            ClockUpdateScheduler.AlarmMode.INEXACT,
            ClockUpdateScheduler.alarmMode(sdkInt = 33, canScheduleExactAlarms = false),
        )
        assertEquals(
            ClockUpdateScheduler.AlarmMode.INEXACT,
            ClockUpdateScheduler.alarmMode(sdkInt = 34, canScheduleExactAlarms = false),
        )
    }

    @Test
    fun deniedPermissionNeverCallsExactAlarm() {
        var exactCalled = false
        var inexactCalled = false

        val used = ClockUpdateScheduler.schedule(
            mode = ClockUpdateScheduler.AlarmMode.INEXACT,
            setExact = { exactCalled = true },
            setInexact = { inexactCalled = true },
        )

        assertFalse("alarm exact tidak boleh dipasang saat izin ditolak", exactCalled)
        assertTrue("alarm inexact harus tetap dipasang", inexactCalled)
        assertEquals(ClockUpdateScheduler.AlarmMode.INEXACT, used)
    }

    @Test
    fun allowedPermissionCallsExactAlarmOnly() {
        var exactCalled = false
        var inexactCalled = false

        val used = ClockUpdateScheduler.schedule(
            mode = ClockUpdateScheduler.AlarmMode.EXACT,
            setExact = { exactCalled = true },
            setInexact = { inexactCalled = true },
        )

        assertTrue(exactCalled)
        assertFalse("tidak perlu alarm inexact bila exact berhasil", inexactCalled)
        assertEquals(ClockUpdateScheduler.AlarmMode.EXACT, used)
    }

    @Test
    fun securityExceptionFromRevokedPermissionFallsBackToInexact() {
        // Izin dicabut tepat di antara canScheduleExactAlarms() dan setExact...
        var inexactCalled = false

        val used = ClockUpdateScheduler.schedule(
            mode = ClockUpdateScheduler.AlarmMode.EXACT,
            setExact = { throw SecurityException("exact alarm not permitted") },
            setInexact = { inexactCalled = true },
        )

        assertTrue("penjadwalan tidak boleh berhenti saat SecurityException", inexactCalled)
        assertEquals(ClockUpdateScheduler.AlarmMode.INEXACT, used)
    }
}
