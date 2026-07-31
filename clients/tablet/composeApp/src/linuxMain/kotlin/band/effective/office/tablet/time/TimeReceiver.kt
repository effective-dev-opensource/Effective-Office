package band.effective.office.tablet.time

import band.effective.office.tablet.feature.main.domain.CurrentTimeHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

/**
 * Aurora implementation of TimeReceiver.
 *
 * There is no system broadcast for time changes (Android) and no NSTimer (iOS) here, so this
 * just ticks once a minute from a coroutine.
 */
actual class TimeReceiver {
    actual val currentTime: StateFlow<LocalDateTime> = CurrentTimeHolder.currentTime

    private val scope = CoroutineScope(Dispatchers.Default)

    init {
        scope.launch {
            while (true) {
                delay(60_000L)
                CurrentTimeHolder.updateTime(
                    Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
                )
            }
        }
    }
}
