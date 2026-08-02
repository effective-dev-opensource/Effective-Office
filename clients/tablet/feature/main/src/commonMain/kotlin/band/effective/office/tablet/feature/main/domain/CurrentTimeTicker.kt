package band.effective.office.tablet.feature.main.domain

import kotlin.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Drives [CurrentTimeHolder]: the screen shows a clock and a "N minutes left" countdown, and
 * something has to move them.
 *
 * The tick loop of the Aurora `TimeReceiver` — Android and iOS are woken by the system instead and
 * hold no timer of their own.
 *
 * The tick is aligned to the wall clock — the first delay runs to the next whole minute, not a
 * minute from whenever the app happened to start — so the displayed time flips at :00 instead of
 * up to 59 seconds late.
 *
 * [onTick] is a parameter rather than a hard-wired call into [CurrentTimeHolder] so that the
 * ticker itself stays free of global state.
 */
class CurrentTimeTicker(
    private val clock: Clock = Clock.System,
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
) {
    fun start(scope: CoroutineScope, onTick: (LocalDateTime) -> Unit): Job = scope.launch {
        while (true) {
            delay(millisToNextMinute())
            onTick(clock.now().toLocalDateTime(timeZone))
        }
    }

    private fun millisToNextMinute(): Long =
        MINUTE_MILLIS - clock.now().toEpochMilliseconds() % MINUTE_MILLIS

    private companion object {
        const val MINUTE_MILLIS = 60_000L
    }
}
