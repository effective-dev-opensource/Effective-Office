package band.effective.office.backend.feature.teammates.core.domain.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

object TeammateStatus {
    const val ACTIVE = "Active"
}

/**
 * Domain model representing a teammate.
 * 
 * This is the core business entity that encapsulates all the essential
 * information about a teammate from the Notion database.
 */
data class Teammate(
    @field:NotBlank(message = "Teammate ID is required")
    val id: String,

    @field:NotBlank(message = "Teammate name is required")
    val name: String,

    @field:NotNull(message = "Positions list is required")
    val positions: List<String>,

    @field:NotBlank(message = "Employment is required")
    val employment: String,

    @field:NotNull(message = "Start date is required")
    val startDate: LocalDate,

    @field:NotNull(message = "Next birthday is required")
    val nextBDay: LocalDate,

    val duolingo: String? = null,

    val photo: String? = null,

    @field:NotBlank(message = "Status is required")
    val status: String
) {
    /**
     * Checks if the teammate is currently active.
     * and their status is "Active".
     */
    fun isActive(): Boolean {
        return status == TeammateStatus.ACTIVE
    }
}