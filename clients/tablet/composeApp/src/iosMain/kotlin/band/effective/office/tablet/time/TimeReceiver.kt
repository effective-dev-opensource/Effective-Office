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
private const val PAST_BOUNDARY_SECONDS = 0.05

/**
 * iOS: an NSTimer on the main run loop, re-aimed at the next whole minute after every fire, so it
 * follows the wall clock instead of drifting away from it on its own 60-second cadence.
 * A clock or zone moved under the app arrives by notification rather than by tick.
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
        ) { firedTimer ->
            publishCurrentTime()
            firedTimer?.fireDate = NSDate.dateWithTimeIntervalSinceNow(secondsToNextMinute())
        }
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

    /**
     * Seconds until just *past* the next whole minute. The offset keeps the fire off the boundary
     * itself, so the clock is never read while the minute that is ending is still current.
     */
    private fun secondsToNextMinute(): Double =
        (MINUTE_MILLIS - Clock.System.now().toEpochMilliseconds() % MINUTE_MILLIS) / 1000.0 +
            PAST_BOUNDARY_SECONDS

    private fun getCurrentTime(): LocalDateTime = currentLocalDateTime
}
