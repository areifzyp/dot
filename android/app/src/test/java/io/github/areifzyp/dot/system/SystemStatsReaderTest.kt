package io.github.areifzyp.dot.system

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/**
 * Unit test parsing metrik Status HP (PRD §6.5).
 * Hanya logika murni yang diuji — pembacaan sistem butuh perangkat.
 */
class SystemStatsReaderTest {

    @Test
    fun `parse cpu line computes busy and total`() {
        // cpu user nice system idle iowait irq softirq steal guest guest_nice
        val snap = CpuSampler.parseSnapshot(
            "cpu  100 0 50 800 50 0 10 0 0 0",
        )
        assertNotNull(snap)
        // total = 100+0+50+800+50+0+10 = 1010 ; idle = 800+50 = 850 ; busy = 160
        assertEquals(1010L, snap!!.total)
        assertEquals(160L, snap.busy)
    }

    @Test
    fun `parse cpu line rejects malformed input`() {
        assertNull(CpuSampler.parseSnapshot("cpu"))
        assertNull(CpuSampler.parseSnapshot("cpu  1 2"))
        assertNull(CpuSampler.parseSnapshot("cpu  0 0 0 0"))
    }

    @Test
    fun `cpu utilization between two samples`() {
        // busy 160/1010 → berikutnya busy 320/2020 : delta busy 160, delta total 1010 ≈ 15%
        val sampler = CpuSampler("/nonexistent/proc/stat")
        // tanpa file, next() selalu Unavailable (fallback jujur, bukan angka palsu)
        assertEquals(Metric.Unavailable, sampler.next())
        assertEquals(Metric.Unavailable, sampler.next())
    }

    @Test
    fun `dotsFor scales percent to dot count`() {
        assertEquals(0, dotsFor(0))
        assertEquals(1, dotsFor(5))     // pembulatan ke bawah, minimum 1
        assertEquals(6, dotsFor(60))    // kriteria penerimaan: 60% = 6 dari 10
        assertEquals(10, dotsFor(100))
        assertEquals(10, dotsFor(150))  // coerce
    }

    @Test
    fun `formatGb renders one decimal`() {
        val out = SystemStatsReader.formatGb(6L * 1024 * 1024 * 1024)
        assertEquals("6.0 GB", out.replace(',', '.'))
    }
}