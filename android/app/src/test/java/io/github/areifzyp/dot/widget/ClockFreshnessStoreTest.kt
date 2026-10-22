package io.github.areifzyp.dot.widget

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Penyimpanan stempel pembaruan terakhir (STATUS-MAP §4a).
 *
 * Yang dijaga: widget baru dipasang tidak boleh langsung menampilkan penanda
 * "tertunda" (tidak ada stempel → `null` → `OnTime`), dan nilai rusak dari
 * prefs tidak boleh lolos menjadi label jam yang salah.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ClockFreshnessStoreTest {

    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun noStampBeforeFirstRender() {
        assertNull("widget yang baru dipasang belum punya stempel", ClockFreshnessStore.load(context))
    }

    @Test
    fun savedStampRoundTrips() {
        val stamp = ClockUpdateStamp(epochMillis = 1_700_000_000_000L, minuteOfDay = 12 * 60 + 5)

        ClockFreshnessStore.save(context, stamp)

        assertEquals(stamp, ClockFreshnessStore.load(context))
    }

    @Test
    fun laterSaveReplacesEarlierStamp() {
        ClockFreshnessStore.save(context, ClockUpdateStamp(1_700_000_000_000L, 12 * 60 + 5))
        val newer = ClockUpdateStamp(1_700_000_060_000L, 12 * 60 + 6)

        ClockFreshnessStore.save(context, newer)

        assertEquals(newer, ClockFreshnessStore.load(context))
    }
}
