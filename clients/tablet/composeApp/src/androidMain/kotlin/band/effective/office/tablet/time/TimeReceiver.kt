package band.effective.office.tablet.time

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import band.effective.office.shared.core.utils.currentLocalDateTime
import band.effective.office.tablet.feature.main.domain.CurrentTimeHolder
import kotlinx.datetime.LocalDateTime

/**
 * Android: `ACTION_TIME_TICK` is the system's own once-a-minute wake-up, so there is no timer here
 * and the flip lands on :00 for free; it cannot be declared in the manifest, only registered at
 * runtime. The context has to be the application one — the receiver outlives any single activity.
 */
actual class TimeReceiver(
    private val context: Context,
    private val currentTimeHolder: CurrentTimeHolder,
) {

    private var registered = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                Intent.ACTION_TIME_TICK,
                Intent.ACTION_TIME_CHANGED,
                Intent.ACTION_TIMEZONE_CHANGED -> {
                    currentTimeHolder.updateTime(getCurrentTime())
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

    private fun getCurrentTime(): LocalDateTime = currentLocalDateTime
}
