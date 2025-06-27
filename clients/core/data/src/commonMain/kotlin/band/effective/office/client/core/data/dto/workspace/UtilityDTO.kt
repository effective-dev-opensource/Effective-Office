package band.effective.office.client.core.data.dto.workspace

import kotlinx.serialization.Serializable

/**
 * Represents a utility available in a workspace.
 * @property id Unique identifier
 * @property name Name of the utility
 * @property iconUrl URL to the utility's icon
 * @property count Number of this utility available
 */
@Serializable
data class UtilityDTO(
    val id: String,
    val name: String,
    val iconUrl: String,
    val count: Int
)