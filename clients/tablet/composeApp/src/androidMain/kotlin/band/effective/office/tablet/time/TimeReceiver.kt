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
 * Android-only: reacts to the clock or the time zone being changed under the app, so the screen
 * does not keep showing the old time until the next tick.
 *
 * The ordinary once-a-minute cadence is not handled here — that is
 * [band.effective.office.tablet.feature.main.domain.CurrentTimeTicker], which runs on every
 * platform. ACTION_TIME_TICK is therefore deliberately not subscribed to: it would only duplicate
 * the ticker.
 */
class TimeReceiver(private val context: Context) {

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED -> {
                    CurrentTimeHolder.updateTime(getCurrentTime())
                }
            }
        }
    }

    /**
     * Registers the broadcast receiver to listen for time-related broadcasts.
     */
    fun register() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_TIME_CHANGED)
            addAction(Intent.ACTION_TIMEZONE_CHANGED)
        }
        context.registerReceiver(receiver, filter)
    }

    /**
     * Unregisters the broadcast receiver.
     */
    fun unregister() {
        context.unregisterReceiver(receiver)
    }

    /**
     * Gets the current time as a LocalDateTime.
     */
    private fun getCurrentTime(): LocalDateTime {
        return Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    }
}
