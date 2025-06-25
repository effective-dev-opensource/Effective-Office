package band.effective.office.backend.feature.booking.core.domain.model

import band.effective.office.backend.core.domain.model.User
import band.effective.office.backend.core.domain.model.Workspace
import java.time.Instant
import java.util.UUID

/**
 * Represents a booking of a workspace.
 * 
 * A booking is created by a user (owner) for a specific workspace and time period.
 * It can optionally have participants, a recurrence pattern, and an ID
 * from a calendar provider.
 * 
 * For recurring bookings, the recurringBookingId field links individual booking instances
 * to their parent recurring booking.
 */
data class Booking(
    val id: String = "",
    val owner: User?,
    val participants: List<User> = emptyList(),
    val workspace: Workspace,
    val beginBooking: Instant,
    val endBooking: Instant,
    val recurrence: RecurrenceModel? = null,
    val recurringBookingId: String? = null // ID of the recurring booking this booking belongs to
)
