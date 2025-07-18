package band.effective.office.tablet.core.data.dto.booking

import band.effective.office.tablet.core.data.dto.user.UserDTO
import band.effective.office.tablet.core.data.dto.workspace.WorkspaceDTO
import kotlinx.serialization.Serializable

/**
 * Represents a response for a booking.
 * @property owner Owner of the booking
 * @property participants List of participants in the booking
 * @property workspace Workspace that is booked
 * @property id Unique identifier of the booking
 * @property beginBooking Start time of the booking (Unix timestamp)
 * @property endBooking End time of the booking (Unix timestamp)
 * @property recurrence Recurrence pattern for recurring bookings
 * @property recurringBookingId ID of the recurring booking series this booking belongs to
 * @property isEditable Flag indicating if the booking can be edited or deleted from tablet client
 */
@Serializable
data class BookingResponseDTO(
    val owner: UserDTO?,
    val participants: List<UserDTO>,
    val workspace: WorkspaceDTO,
    val id: String,
    val beginBooking: Long,
    val endBooking: Long,
    val recurrence: RecurrenceDTO? = null,
    val recurringBookingId: String? = null,
    val isEditable: Boolean = true
)
