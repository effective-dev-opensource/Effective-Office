package band.effective.office.backend.feature.teammates.core.domain.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * Domain model representing a teammate score (Supernova points).
 */
data class TeammateScore(
    @field:NotBlank(message = "Teammate ID is required")
    val id: String,

    @field:NotNull(message = "Score is required")
    val score: Int
)


