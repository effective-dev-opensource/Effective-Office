package band.effective.office.tablet.core.domain.model

import kotlin.time.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable

/**
 * Domain model representing a room.
 */
@Serializable
data class RoomInfo(
    val name: String,
    val capacity: Int,
    val isHaveTv: Boolean,
    val socketCount: Int,
    val eventList: List<EventInfo>,
    val currentEvent: EventInfo?, //NOTE(Maksim Mishenko): currentEvent is null if room is free
    val id: String
) {
    companion object {
        val defaultValue =
            RoomInfo(
                name = "Default",
                capacity = 0,
                isHaveTv = false,
                socketCount = 0,
                eventList = listOf(),
                currentEvent = null,
                id = ""
            )
    }
}

fun RoomInfo.nextEvent(): EventInfo? {
    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
    val tomorrow = LocalDateTime((now.date + DatePeriod(days = 1)), LocalTime(0,0))
    return eventList.filter { it.startTime > now && it.startTime < tomorrow }.minByOrNull { it.startTime }
}
