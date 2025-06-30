package band.effective.office.tablet.core.domain.model

import kotlin.time.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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
            startTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            finishTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
            organizer = Organizer.Companion.default,
            id = defaultId,
            isLoading = true,
        )
    }

    fun isNotCreated() = id == defaultId
}
