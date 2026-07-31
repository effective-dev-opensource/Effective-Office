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
 * Ни системных броадкастов о смене времени (Android), ни NSTimer (iOS) здесь нет,
 * поэтому просто тикаем раз в минуту из корутины.
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
