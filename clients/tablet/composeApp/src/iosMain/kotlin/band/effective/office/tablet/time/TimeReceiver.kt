package band.effective.office.tablet.time

import band.effective.office.tablet.feature.main.domain.CurrentTimeHolder
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSDefaultRunLoopMode
import platform.Foundation.NSRunLoop
import platform.Foundation.NSDate
import platform.Foundation.NSTimer
import platform.Foundation.timeIntervalSince1970

/**
 * iOS implementation of TimeReceiver that uses a timer to update the current time.
 */
actual class TimeReceiver {
    actual val currentTime: StateFlow<LocalDateTime> = CurrentTimeHolder.currentTime

    private var timer: NSTimer? = null

    init {
        // Update time every minute
        timer = NSTimer.scheduledTimerWithTimeInterval(
            60.0, // 60 seconds
            true, // repeats
            {
                CurrentTimeHolder.updateTime(getCurrentTime())
            }
        )

        // Add timer to the main run loop
        NSRunLoop.mainRunLoop.addTimer(timer!!, NSDefaultRunLoopMode)
    }

    /**
     * Gets the current time as a LocalDateTime.
     */
    private fun getCurrentTime(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
}
