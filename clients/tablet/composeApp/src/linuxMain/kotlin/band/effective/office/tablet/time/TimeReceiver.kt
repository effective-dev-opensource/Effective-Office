package band.effective.office.tablet.time

import band.effective.office.tablet.feature.main.domain.CurrentTimeHolder
import band.effective.office.tablet.feature.main.domain.CurrentTimeTicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * Aurora: no time broadcast and no run loop of our own to hang a timer on, so a coroutine that
 * sleeps to the next whole minute is what is left. [CurrentTimeTicker] holds that loop, including
 * the alignment — without it the minute would flip a minute after launch rather than at :00.
 */
actual class TimeReceiver {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var job: Job? = null

    actual fun start() {
        if (job != null) return
        job = CurrentTimeTicker().start(scope, CurrentTimeHolder::updateTime)
    }

    actual fun stop() {
        job?.cancel()
        job = null
    }
}
