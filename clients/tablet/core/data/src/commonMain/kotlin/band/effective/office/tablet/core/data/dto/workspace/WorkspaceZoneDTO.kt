package band.effective.office.tablet.core.data.dto.workspace

import kotlinx.serialization.Serializable

/**
 * Represents a zone in the workspace.
 * @property id Unique identifier
 * @property name Name of the zone
 */
@Serializable
data class WorkspaceZoneDTO(
    val id: String,
    val name: String
)