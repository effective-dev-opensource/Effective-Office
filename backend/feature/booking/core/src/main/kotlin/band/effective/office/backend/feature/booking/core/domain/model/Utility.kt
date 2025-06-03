package band.effective.office.backend.feature.booking.core.domain.model

import java.util.UUID

/**
 * Represents a utility available in a workspace.
 */
data class Utility(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val iconUrl: String,
    val count: Int
)