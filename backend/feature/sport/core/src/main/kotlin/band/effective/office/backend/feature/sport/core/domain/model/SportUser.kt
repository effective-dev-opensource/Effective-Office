package band.effective.office.backend.feature.sport.core.domain.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * Domain model representing a sport user.
 * Information about a sport user and tracked time.
 */
data class SportUser(
    @field:NotBlank(message = "User name is required")
    val name: String,

    @field:NotBlank(message = "User email is required")
    val email: String,

    @field:NotNull(message = "Total seconds is required")
    val totalSeconds: Int
)