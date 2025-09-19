package band.effective.office.backend.feature.leader.id.domain.model

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.time.LocalDateTime

/**
 * Domain model representing a LeaderId event.
 * 
 * This is the core business entity that encapsulates all the essential
 * information about an event from the LeaderId platform.
 */
data class LeaderIdEvent(
    @field:Positive(message = "Event ID must be positive")
    val id: Int,

    @field:NotBlank(message = "Event name is required")
    val name: String,

    @field:NotNull(message = "Start date time is required")
    val startDateTime: LocalDateTime,

    @field:NotNull(message = "Finish date time is required")
    val finishDateTime: LocalDateTime,

    val isOnline: Boolean = false,

    val photoUrl: String? = null,

    val organizer: String? = null,

    val speakers: List<String> = emptyList(),

    val endRegDate: LocalDateTime? = null
) {
    init {
        require(startDateTime.isBefore(finishDateTime)) {
            "Start date time must be before finish date time"
        }
    }
}
