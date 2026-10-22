package io.github.areifzyp.dot.widget

import org.junit.Assert.assertEquals
import org.junit.Test

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
}