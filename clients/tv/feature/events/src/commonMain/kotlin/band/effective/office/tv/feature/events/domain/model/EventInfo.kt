package band.effective.office.tv.feature.events.domain.model

import kotlinx.datetime.LocalDateTime

/**
 * Event domain model for slideshow.
 */
data class EventInfo(
    val id: Int,
    val name: String,
    val startDateTime: LocalDateTime,
    val finishDateTime: LocalDateTime,
    val isOnline: Boolean,
    val photoUrl: String?,
    val organizer: String? = null,
    val speakers: List<String> = emptyList(),
    val endRegDate: LocalDateTime? = null,
    val location: String? = null,
)
