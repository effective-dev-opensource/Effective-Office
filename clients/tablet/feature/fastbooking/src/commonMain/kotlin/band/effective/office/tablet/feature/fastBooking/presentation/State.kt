package band.effective.office.tablet.feature.fastBooking.presentation

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.shared.core.utils.currentLocalDateTime
import kotlinx.datetime.LocalDateTime

/**
 * State for the FastBookingViewModel.
 *
 * [modal] replaces the former Decompose `ModalConfig` child stack — it is now plain state
 * describing which modal view to render.
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

/** Which modal view the fast-booking flow is currently showing. Was `FastBookingComponent.ModalConfig`. */
sealed interface FastBookingModal {
    /** Shown while searching / creating the booking. */
    data object Loading : FastBookingModal

    /** Shown when a booking is successfully created. */
    data class Success(val room: String, val eventInfo: EventInfo) : FastBookingModal

    /** Shown when a booking cannot be created (either an error or no available room). */
    data class Failure(val room: String) : FastBookingModal
}
