package band.effective.office.tablet.feature.main.domain

import kotlin.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import band.effective.office.shared.core.utils.defaultTimeZone
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
 *
 * [timeZone] is asked on every tick rather than captured once, and that is the whole point of it
 * being a function. Held as a value it survives the very change it exists to notice: the instant
 * keeps moving, so the clock goes on ticking and looks healthy, while every conversion still uses
 * the zone the app was started in — the displayed time is then wrong by the offset, for as long as
 * the app runs. This loop is Aurora's alone, and Aurora is also the platform with no system
 * notification to fall back on, so there a captured zone is wrong until somebody restarts the app.
 */
class CurrentTimeTicker(
    private val clock: Clock = Clock.System,
    private val timeZone: () -> TimeZone = { defaultTimeZone },
) {
    fun start(scope: CoroutineScope, onTick: (LocalDateTime) -> Unit): Job = scope.launch {
        while (true) {
            delay(millisToNextMinute())
            onTick(clock.now().toLocalDateTime(timeZone()))
        }
    }

    private fun millisToNextMinute(): Long =
        MINUTE_MILLIS - clock.now().toEpochMilliseconds() % MINUTE_MILLIS

    private companion object {
        const val MINUTE_MILLIS = 60_000L
    }
}
