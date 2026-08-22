package band.effective.office.tablet.time

import band.effective.office.shared.core.utils.currentLocalDateTime
import band.effective.office.tablet.feature.main.domain.CurrentTimeHolder
import kotlin.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val MINUTE_MILLIS = 60_000L
private const val PAST_BOUNDARY_MILLIS = 50L

/**
 * Aurora offers neither Android's time-tick broadcast nor NSTimer, so the tick is ours: a coroutine
 * that sleeps to the next whole minute rather than for a minute, since this runs for days on a wall
 * and the header clock has to stay on the wall clock.
 */
actual class TimeReceiver(
    private val currentTimeHolder: CurrentTimeHolder,
) {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var job: Job? = null

    actual fun start() {
        if (job != null) return
        job = scope.launch {
            while (isActive) {
                delay(millisToNextMinute())
                currentTimeHolder.updateTime(currentLocalDateTime)
            }
        }
    }

    actual fun stop() {
        job?.cancel()
        job = null
    }

    /**
     * Milliseconds until just *past* the next whole minute, so the clock is never read while the
     * minute that is ending is still the current one.
     */
    private fun millisToNextMinute(): Long =
        MINUTE_MILLIS - Clock.System.now().toEpochMilliseconds() % MINUTE_MILLIS + PAST_BOUNDARY_MILLIS
}
