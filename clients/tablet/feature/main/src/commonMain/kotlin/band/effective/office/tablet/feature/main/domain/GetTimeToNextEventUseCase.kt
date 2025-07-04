package band.effective.office.tablet.feature.main.domain

import band.effective.office.tablet.core.domain.model.RoomInfo
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class GetTimeToNextEventUseCase {

    operator fun invoke(rooms: List<RoomInfo>, selectedRoomIndex: Int): Int {
        val now = Clock.System.now()
        val timeZone = TimeZone.currentSystemDefault()
        val currentEvent = rooms.getOrNull(selectedRoomIndex)?.currentEvent ?: return 0
        val finishInstant = currentEvent.finishTime.toInstant(timeZone)

        return ((finishInstant - now).inWholeMinutes).toInt()
    }
}