package band.effective.office.backend.feature.booking.core.domain.model

import band.effective.office.backend.core.domain.model.User
import band.effective.office.backend.core.domain.model.Workspace
import java.time.Instant
import java.util.UUID

/**
 * Represents a booking of a workspace.
 */
data class Booking(
    val id: UUID = UUID.randomUUID(),
    val owner: User,
    val participants: List<User> = emptyList(),
    val workspace: Workspace,
    val beginBooking: Instant,
    val endBooking: Instant,
    val recurrence: RecurrenceModel? = null,
    val externalEventId: String? = null // ID returned by the calendar provider
)