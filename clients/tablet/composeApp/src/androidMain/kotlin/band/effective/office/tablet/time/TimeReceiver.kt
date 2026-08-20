package band.effective.office.tablet.time

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import band.effective.office.tablet.feature.main.domain.CurrentTimeHolder
import kotlinx.coroutines.flow.StateFlow
import kotlin.time.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/**
 * A broadcast receiver that listens for time-related broadcasts and emits the current time.
 */
actual class TimeReceiver(private val context: Context) {

    actual val currentTime: StateFlow<LocalDateTime> = CurrentTimeHolder.currentTime

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_TIME_TICK, Intent.ACTION_TIME_CHANGED -> {
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
            addAction(Intent.ACTION_TIME_TICK)
            addAction(Intent.ACTION_TIME_CHANGED)
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
