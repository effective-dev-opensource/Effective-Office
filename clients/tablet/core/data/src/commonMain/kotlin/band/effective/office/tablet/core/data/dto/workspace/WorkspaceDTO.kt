package band.effective.office.tablet.core.data.dto.workspace

import band.effective.office.tablet.core.data.dto.booking.BookingResponseDTO
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
    val bookings: List<BookingResponseDTO>? = null
)
