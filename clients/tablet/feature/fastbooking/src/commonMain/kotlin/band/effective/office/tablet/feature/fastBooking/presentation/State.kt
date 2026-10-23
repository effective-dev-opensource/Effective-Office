package band.effective.office.tablet.feature.fastBooking.presentation

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.shared.core.utils.currentLocalDateTime
import kotlinx.datetime.LocalDateTime

/**
 * State for the FastBookingViewModel.
 */
data class State(
    val isLoad: Boolean,
    val isSuccess: Boolean,
    val isError: Boolean,
    val event: EventInfo,
    val minutesLeft: Int,
    val currentTime: LocalDateTime,
    val modal: FastBookingModal,
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
                modal = FastBookingModal.Loading,
            )
    }
}

/** Which view the fast-booking flow is currently showing. */
sealed interface FastBookingModal {
    data object Loading : FastBookingModal

    data class Success(val room: String, val eventInfo: EventInfo) : FastBookingModal

    data class Failure(val room: String) : FastBookingModal
}
