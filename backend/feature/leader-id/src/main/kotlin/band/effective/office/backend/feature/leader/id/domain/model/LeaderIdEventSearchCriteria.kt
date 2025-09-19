package band.effective.office.backend.feature.leader.id.domain.model

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDate

/**
 * Domain model representing search criteria for LeaderId events.
 * 
 * Encapsulates all parameters needed to search for events in the LeaderId API.
 */
data class LeaderIdEventSearchCriteria(
    @field:NotNull(message = "Date from is required")
    val dateFrom: LocalDate,

    @field:NotNull(message = "Date to is required")
    val dateTo: LocalDate,

    @field:Positive(message = "City ID must be positive")
    val cityId: Int,

    @field:Positive(message = "Place ID must be positive")
    val placeId: Int,

    @field:Positive(message = "Pagination size must be positive")
    val paginationSize: Int
) {
    init {
        require(dateFrom.isBefore(dateTo) || dateFrom.isEqual(dateTo)) {
            "Date from must be before or equal to date to"
        }
    }
}
