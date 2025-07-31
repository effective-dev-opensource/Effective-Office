package band.effective.office.tablet.time

import kotlinx.coroutines.flow.StateFlow
import kotlinx.datetime.LocalDateTime

/**
 * A receiver that emits the current time.
 */
expect class TimeReceiver {
    /**
     * A flow that emits the current time.
     */
    val currentTime: StateFlow<LocalDateTime>
}
