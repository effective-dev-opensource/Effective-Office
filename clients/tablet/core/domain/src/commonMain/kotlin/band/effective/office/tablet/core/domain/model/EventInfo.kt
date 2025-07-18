package band.effective.office.tablet.core.domain.model

import band.effective.office.tablet.core.domain.util.currentLocalDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

/**
 * Domain model representing an event or booking.
 */
@Serializable
data class EventInfo(
    val startTime: LocalDateTime,
    val finishTime: LocalDateTime,
    val organizer: Organizer,
    val id: String,
    val isLoading: Boolean,
    val isEditable: Boolean = true,
) {
    init {
        // Validate that start time is before finish time
        require(startTime <= finishTime) { "Start time must be before or equal to finish time" }
    }

    companion object {
        const val defaultId: String = ""

        val emptyEvent = EventInfo(
            startTime = currentLocalDateTime,
            finishTime = currentLocalDateTime,
            organizer = Organizer.Companion.default,
            id = defaultId,
            isLoading = true,
        )
    }

    fun isNotCreated() = id == defaultId
}
