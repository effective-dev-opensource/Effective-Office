package band.effective.office.tablet.feature.main.domain

import band.effective.office.shared.core.utils.defaultTimeZone
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.model.nextEvent
import kotlin.time.Clock
import kotlinx.datetime.toInstant

private const val SECONDS_PER_MINUTE = 60

class GetTimeToNextEventUseCase {

    /**
     * Minutes left, rounded up so that the header clock plus this number is the booking's end.
     * The header shows the minute it is in, so a rounded-down remainder reads one minute short of
     * the end for 59 seconds out of every 60.
     */
    operator fun invoke(rooms: List<RoomInfo>, selectedRoomIndex: Int): Int {
        val now = Clock.System.now()
        val timeZone = defaultTimeZone
        val room = rooms.getOrNull(selectedRoomIndex) ?: return 0
        val currentEvent = room.currentEvent
        val nextEvent = room.nextEvent()
        val finishInstant = when {
            currentEvent != null -> currentEvent.finishTime.toInstant(timeZone)
            nextEvent != null -> nextEvent.startTime.toInstant(timeZone)
            else -> return 0
        }

        val secondsLeft = (finishInstant - now).inWholeSeconds
        if (secondsLeft <= 0) return 0

        return ((secondsLeft + SECONDS_PER_MINUTE - 1) / SECONDS_PER_MINUTE).toInt()
    }
}