package band.effective.office.backend.core.domain.model

import java.util.*

/**
 * Domain model representing a workspace in the office.
 *
 * @property id The unique identifier for the workspace
 * @property name The name of the workspace
 * @property tag The tag for categorizing the workspace
 * @property utilities List of utilities available in the workspace
 * @property zone The zone where the workspace is located
 */
data class Workspace(
    var id: UUID?,
    var name: String,
    var tag: String,
    var utilities: List<Utility>,
    var zone: WorkspaceZone? = null
)