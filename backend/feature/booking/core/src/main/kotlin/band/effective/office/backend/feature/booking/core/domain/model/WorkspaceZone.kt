package band.effective.office.backend.feature.booking.core.domain.model

import java.util.UUID

/**
 * Represents a zone in the office where workspaces are located.
 */
data class WorkspaceZone(
    val id: UUID = UUID.randomUUID(),
    val name: String
)