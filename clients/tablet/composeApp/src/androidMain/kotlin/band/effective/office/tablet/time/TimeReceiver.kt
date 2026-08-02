package band.effective.office.tablet.time

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import band.effective.office.tablet.feature.main.domain.CurrentTimeHolder
import kotlin.time.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * Android: the system already sends a broadcast on every whole minute, so there is no timer here
 * at all — the cheapest of the three implementations, and aligned to :00 for free.
 *
 * - `ACTION_TIME_TICK` is the minute cadence. It cannot be declared in the manifest; a receiver
 *   registered at runtime is the only way to get it.
 * - `ACTION_TIME_CHANGED` and `ACTION_TIMEZONE_CHANGED` cover the clock or the zone being moved
 *   under the app, so the screen does not keep showing the old time until the next tick.
 *
 * The context is the application context (see `androidContext(applicationContext)` in `App`), not
 * an activity — the receiver outlives any single activity and would otherwise leak one.
 */
actual class TimeReceiver(private val context: Context) {

    private var registered = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_TIME_TICK,
                Intent.ACTION_TIME_CHANGED,
                Intent.ACTION_TIMEZONE_CHANGED -> {
                    CurrentTimeHolder.updateTime(getCurrentTime())
                }
            }
        }
    }

    actual fun start() {
        if (registered) return
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }
        context.registerReceiver(receiver, filter)
        registered = true
    }

    actual fun stop() {
        if (!registered) return
        context.unregisterReceiver(receiver)
        registered = false
    }

    private fun getCurrentTime(): LocalDateTime =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
}
