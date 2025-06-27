package band.effective.office.client.core.data.dto.booking

import kotlinx.serialization.Serializable

/**
 * Represents a request to book a workspace.
 * @property ownerEmail Email of the booking owner
 * @property participantEmails List of participant emails
 * @property workspaceId ID of the workspace to book
 * @property beginBooking Start time of the booking (Unix timestamp)
 * @property endBooking End time of the booking (Unix timestamp)
 * @property recurrence Recurrence pattern for recurring bookings
 */
@Serializable
data class BookingRequestDTO(
    val ownerEmail: String?,
    val participantEmails: List<String>,
    val workspaceId: String,
    val beginBooking: Long,
    val endBooking: Long,
    val recurrence: RecurrenceDTO? = null
)