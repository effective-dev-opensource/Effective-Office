package band.effective.office.tablet.core.ui.inactivity

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Singleton manager that tracks user inactivity across the application.
 * It resets a timer whenever there is any UI interaction (touch event, key event)
 * and executes a callback after a specified period of inactivity.
 */
object InactivityManager {
    /**
     * Default inactivity timeout in milliseconds (1 minute)
     */
    const val DEFAULT_INACTIVITY_TIMEOUT_MS = 60000L

    private val handler = Handler(Looper.getMainLooper())
    private var inactivityTimeoutMs = DEFAULT_INACTIVITY_TIMEOUT_MS
    private var inactivityCallback: (() -> Unit)? = null
    private var isTracking = false

    private val _inactivityState = MutableStateFlow(false)
    val inactivityState: StateFlow<Boolean> = _inactivityState.asStateFlow()

    private val inactivityRunnable = Runnable {
        _inactivityState.value = true
        inactivityCallback?.invoke()
    }

    /**
     * Starts tracking user inactivity.
     *
     * @param timeoutMs The inactivity timeout in milliseconds. Default is 1 minute.
     * @param callback The callback to execute when inactivity is detected.
     */
    fun startTracking(
        timeoutMs: Long = DEFAULT_INACTIVITY_TIMEOUT_MS,
        callback: (() -> Unit)? = null
    ) {
        inactivityTimeoutMs = timeoutMs
        inactivityCallback = callback
        isTracking = true
        resetTimer()
    }

    /**
     * Stops tracking user inactivity.
     */
    fun stopTracking() {
        isTracking = false
        handler.removeCallbacks(inactivityRunnable)
        _inactivityState.value = false
    }

    /**
     * Resets the inactivity timer. This should be called whenever a user interaction is detected.
     */
    fun resetTimer() {
        if (!isTracking) return

        handler.removeCallbacks(inactivityRunnable)
        _inactivityState.value = false
        handler.postDelayed(inactivityRunnable, inactivityTimeoutMs)
    }

    /**
     * Notifies the manager that a user interaction has occurred.
     * This will reset the inactivity timer.
     */
    fun onUserInteraction() {
        resetTimer()
    }
}
