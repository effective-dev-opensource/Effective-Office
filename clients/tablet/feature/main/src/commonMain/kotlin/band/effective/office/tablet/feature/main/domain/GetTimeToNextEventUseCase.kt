package band.effective.office.tablet.feature.main.domain

import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.model.nextEvent
import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class GetTimeToNextEventUseCase {

    operator fun invoke(rooms: List<RoomInfo>, selectedRoomIndex: Int): Int {
        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        val room = rooms.getOrNull(selectedRoomIndex) ?: return 0
        val currentEvent = room.currentEvent
        val nextEvent = room.nextEvent()
        val finishInstant = when {
            currentEvent != null -> currentEvent.finishTime.toInstant(timeZone)
            nextEvent != null -> nextEvent.startTime.toInstant(timeZone)
            else -> return 0
        }

        return ((finishInstant - now).inWholeMinutes).toInt()
    }
}