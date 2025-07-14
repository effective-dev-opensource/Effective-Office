package band.effective.office.backend.core.domain.model

import java.util.*

/**
 * Domain model representing a utility available in a workspace.
 *
 * @property id The unique identifier for the utility
 * @property name The name of the utility
 * @property iconUrl The URL for the utility's icon
 * @property count The quantity of this utility
 */
data class Utility(
    val id: UUID,
    val name: String,
    val iconUrl: String,
    val count: Int
)