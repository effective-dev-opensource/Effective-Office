package band.effective.office.backend.feature.booking.core.domain.model

import java.util.UUID

/**
 * Represents a workspace in the office that can be booked.
 */
data class Workspace(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val tag: String,
    val utilities: List<Utility> = emptyList(),
    val zone: WorkspaceZone? = null
)