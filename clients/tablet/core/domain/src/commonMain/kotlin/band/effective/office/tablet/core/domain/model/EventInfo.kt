package band.effective.office.tablet.core.domain.model

import band.effective.office.tablet.core.domain.util.currentLocalDateTime
import kotlin.time.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class EventInfo(
    val startTime: LocalDateTime,
    val finishTime: LocalDateTime,
    val organizer: Organizer,
    val id: String,
    var isLoading: Boolean,
) {
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
