package band.effective.office.client.core.data.dto.workspace

import kotlinx.serialization.Serializable

/**
 * Represents a workspace.
 * @property id Unique identifier
 * @property name Name of the workspace
 * @property utilities List of utilities available in the workspace
 * @property zone Zone where the workspace is located
 * @property tag Type of workspace (e.g., meeting, regular)
 * @property bookings List of bookings for this workspace (optional)
 */
@Serializable
data class WorkspaceDTO(
    val id: String,
    val name: String,
    val utilities: List<UtilityDTO>,
    val zone: WorkspaceZoneDTO? = null,
    val tag: String,
    val bookings: List<BookingInfo>? = null
)

/**
 * Simplified booking information to avoid circular dependency with BookingResponseDTO.
 * @property id Booking identifier
 * @property beginBooking Start time of the booking
 * @property endBooking End time of the booking
 */
@Serializable
data class BookingInfo(
    val id: String,
    val beginBooking: Long,
    val endBooking: Long
)