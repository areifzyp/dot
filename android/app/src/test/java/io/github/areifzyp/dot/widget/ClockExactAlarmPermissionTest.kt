package io.github.areifzyp.dot.widget

import android.app.AlarmManager
import android.content.Context
import android.os.SystemClock
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowAlarmManager

/**
 * STATUS-MAP §4a AC-2, jalur sungguhan: `scheduleNextUpdate` benar-benar
 * memanggil AlarmManager. Robolectric mencatat alarm yang terpasang, jadi
 * cabang "izin exact alarm ditolak" diverifikasi sampai ke efek sampingnya
 * (alarm inexact tetap terjadwal), bukan hanya ke nilai balik fungsi.
 *
 * `setExactAndAllowWhileIdle` ditandai `allowWhileIdle = true` oleh shadow,
 * sedangkan `set(...)` tidak — itulah yang membedakan kedua jalur di sini.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ClockExactAlarmPermissionTest {

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val alarmManager: AlarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    @Before
    fun setUp() {
        ShadowAlarmManager.setCanScheduleExactAlarms(true)
    }

    @After
    fun tearDown() {
        // Static di shadow: kembalikan ke default agar tidak bocor ke test lain.
        ShadowAlarmManager.setCanScheduleExactAlarms(true)
    }

    @Test
    fun permissionDeniedStillSchedulesAnAlarm() {
        ShadowAlarmManager.setCanScheduleExactAlarms(false)
        val before = SystemClock.elapsedRealtime()

        ClockUpdateScheduler.scheduleNextUpdate(context)

        val alarms = shadowOf(alarmManager).scheduledAlarms
        assertEquals("penjadwalan tidak boleh berhenti saat izin ditolak", 1, alarms.size)
        val alarm = alarms[0]
        assertEquals(AlarmManager.ELAPSED_REALTIME_WAKEUP, alarm.type)
        assertFalse(
            "harus lewat am.set(...) (inexact), bukan setExactAndAllowWhileIdle",
            alarm.allowWhileIdle,
        )
        assertTrue("alarm harus di masa depan", alarm.triggerAtTime > before)
        assertTrue(
            "alarm berikutnya paling lama satu menit dari sekarang",
            alarm.triggerAtTime <= before + 60_000L,
        )
    }

    @Test
    fun permissionGrantedSchedulesExactAlarm() {
        ShadowAlarmManager.setCanScheduleExactAlarms(true)

        ClockUpdateScheduler.scheduleNextUpdate(context)

        val alarms = shadowOf(alarmManager).scheduledAlarms
        assertEquals(1, alarms.size)
        assertTrue(
            "dengan izin, jalurnya setExactAndAllowWhileIdle",
            alarms[0].allowWhileIdle,
        )
    }
}
