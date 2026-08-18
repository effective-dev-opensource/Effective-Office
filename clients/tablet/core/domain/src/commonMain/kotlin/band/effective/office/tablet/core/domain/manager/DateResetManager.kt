package band.effective.office.tablet.core.domain.manager

import band.effective.office.shared.core.utils.currentLocalDateTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDateTime

/**
 * Carries an inactivity timeout from whoever detects it to whoever holds the selected date.
 */
class DateResetManager {
    // Create a CoroutineScope with a SupervisorJob to handle suspending functions
    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Use a suspending function type for the callback
    private var dateResetCallback: (suspend (LocalDateTime) -> Unit)? = null

    /**
     * Registers a callback that will be called when the date needs to be reset.
     *
     * @param callback The callback to execute when the date needs to be reset.
     *                 The callback receives the current local date and time.
     */
    fun registerDateResetCallback(callback: suspend (LocalDateTime) -> Unit) {
        dateResetCallback = callback
    }

    /**
     * Unregisters the date reset callback.
     */
    fun unregisterDateResetCallback() {
        dateResetCallback = null
    }

    /**
     * Triggers a date reset with the current local date and time.
     * This will call the registered callback if one exists.
     */
    fun resetDate() {
        val callback = dateResetCallback ?: return

        // Launch a coroutine to execute the suspending callback
        coroutineScope.launch {
            callback(currentLocalDateTime)
        }
    }
}
