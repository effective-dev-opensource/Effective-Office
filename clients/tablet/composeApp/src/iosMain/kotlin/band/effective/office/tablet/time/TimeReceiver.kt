package band.effective.office.tablet.time

import band.effective.office.tablet.feature.main.domain.CurrentTimeHolder
import kotlin.time.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import platform.Foundation.NSDate
import platform.Foundation.NSDefaultRunLoopMode
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSRunLoop
import platform.Foundation.NSSystemClockDidChangeNotification
import platform.Foundation.NSTimer
import platform.Foundation.dateWithTimeIntervalSinceNow
import platform.darwin.NSObjectProtocol

private const val MINUTE_SECONDS = 60.0
private const val MINUTE_MILLIS = 60_000L

/**
 * iOS: an NSTimer on the main run loop, with its first fire pushed to the next whole minute so the
 * displayed time flips at :00 rather than a minute after launch.
 *
 * A repeating timer keeps its own cadence and does not follow the wall clock, so
 * `NSSystemClockDidChange` is observed as well — that is iOS's equivalent of Android's
 * `ACTION_TIME_CHANGED`, and without it moving the clock stays invisible until the next tick.
 */
actual class TimeReceiver {

    private var timer: NSTimer? = null
    private var clockChangeObserver: NSObjectProtocol? = null

    actual fun start() {
        if (timer != null) return
        val newTimer = NSTimer.timerWithTimeInterval(
            interval = MINUTE_SECONDS,
            repeats = true,
        ) { _ -> publishCurrentTime() }
        newTimer.fireDate = NSDate.dateWithTimeIntervalSinceNow(secondsToNextMinute())
        NSRunLoop.mainRunLoop.addTimer(newTimer, NSDefaultRunLoopMode)
        timer = newTimer

        clockChangeObserver = NSNotificationCenter.defaultCenter.addObserverForName(
            name = NSSystemClockDidChangeNotification,
            `object` = null,
            queue = NSOperationQueue.mainQueue,
        ) { _ -> publishCurrentTime() }
    }

    actual fun stop() {
        timer?.invalidate()
        timer = null
        clockChangeObserver?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
        clockChangeObserver = null
    }

    private fun publishCurrentTime() {
        CurrentTimeHolder.updateTime(getCurrentTime())
    }

    private fun secondsToNextMinute(): Double =
        (MINUTE_MILLIS - Clock.System.now().toEpochMilliseconds() % MINUTE_MILLIS) / 1000.0

    private fun getCurrentTime(): LocalDateTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}
