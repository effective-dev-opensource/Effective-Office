package band.effective.office.tablet.time

import band.effective.office.shared.core.utils.currentLocalDateTime
import band.effective.office.tablet.feature.main.domain.CurrentTimeHolder
import kotlin.time.Clock
import kotlinx.datetime.LocalDateTime
import platform.Foundation.NSDate
import platform.Foundation.NSDefaultRunLoopMode
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSRunLoop
import platform.Foundation.NSSystemClockDidChangeNotification
import platform.Foundation.NSSystemTimeZoneDidChangeNotification
import platform.Foundation.NSTimer
import platform.Foundation.dateWithTimeIntervalSinceNow
import platform.darwin.NSObjectProtocol

private const val MINUTE_SECONDS = 60.0
private const val MINUTE_MILLIS = 60_000L

/**
 * iOS: an NSTimer on the main run loop, its first fire pushed to the next whole minute so the time
 * flips at :00 rather than a minute after launch. The timer keeps its own cadence and does not
 * follow the wall clock, so a clock or zone moved under the app arrives by notification instead.
 */
actual class TimeReceiver(
    private val currentTimeHolder: CurrentTimeHolder,
) {

    private var timer: NSTimer? = null
    private val observers = mutableListOf<NSObjectProtocol>()

    actual fun start() {
        if (timer != null) return
        val newTimer = NSTimer.timerWithTimeInterval(
            interval = MINUTE_SECONDS,
            repeats = true,
        ) { _ -> publishCurrentTime() }
        newTimer.fireDate = NSDate.dateWithTimeIntervalSinceNow(secondsToNextMinute())
        NSRunLoop.mainRunLoop.addTimer(newTimer, NSDefaultRunLoopMode)
        timer = newTimer

        observe(NSSystemClockDidChangeNotification)
        observe(NSSystemTimeZoneDidChangeNotification)
    }

    actual fun stop() {
        timer?.invalidate()
        timer = null
        observers.forEach { NSNotificationCenter.defaultCenter.removeObserver(it) }
        observers.clear()
    }

    private fun observe(notificationName: String?) {
        observers += NSNotificationCenter.defaultCenter.addObserverForName(
            name = notificationName,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
        ) { _ -> publishCurrentTime() }
    }

    private fun publishCurrentTime() {
        currentTimeHolder.updateTime(getCurrentTime())
    }

    private fun secondsToNextMinute(): Double =
        (MINUTE_MILLIS - Clock.System.now().toEpochMilliseconds() % MINUTE_MILLIS) / 1000.0

    private fun getCurrentTime(): LocalDateTime = currentLocalDateTime
}
