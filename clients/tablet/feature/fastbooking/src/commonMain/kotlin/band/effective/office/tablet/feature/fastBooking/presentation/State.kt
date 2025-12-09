package band.effective.office.tablet.feature.fastBooking.presentation

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.shared.core.utils.currentLocalDateTime
import kotlinx.datetime.LocalDateTime

/**
 * State for the FastBookingComponent.
 */
data class State(
    val isLoad: Boolean,
    val isSuccess: Boolean,
    val isError: Boolean,
    val event: EventInfo,
    val minutesLeft: Int,
    val currentTime: LocalDateTime,
) {
    companion object {
        val defaultState =
            State(
                isLoad = true,
                isSuccess = false,
                isError = false,
                event = EventInfo.emptyEvent,
                minutesLeft = 0,
                currentTime = currentLocalDateTime,
            )
    }
}
