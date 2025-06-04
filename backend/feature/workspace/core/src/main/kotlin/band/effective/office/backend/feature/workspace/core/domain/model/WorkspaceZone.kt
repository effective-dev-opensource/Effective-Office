package band.effective.office.backend.feature.workspace.core.domain.model

import java.util.*

/**
 * Domain model representing a zone in the office where workspaces are located.
 *
 * @property id The unique identifier for the workspace zone
 * @property name The name of the workspace zone
 */
data class WorkspaceZone(
    var id: UUID,
    var name: String
)