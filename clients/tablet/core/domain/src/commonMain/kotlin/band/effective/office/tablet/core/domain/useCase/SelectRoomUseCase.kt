package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.util.asInstant
import band.effective.office.tablet.core.domain.util.currentInstant
import kotlin.math.absoluteValue
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

open class SelectRoomUseCase(
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
    private val clock: Clock = Clock.System
) {

    open fun getRoom(currentRoom: RoomInfo, rooms: List<RoomInfo>, minEventDuration: Int): RoomInfo? {
        val candidates = rooms.filter { it.isFreeOn(minEventDuration) }
            .sortedBy { (it.capacity - currentRoom.capacity).absoluteValue }
        return if (candidates.contains(currentRoom)) currentRoom else candidates.firstOrNull()
    }

    fun getNearestFreeRoom(rooms: List<RoomInfo>, minDuration: Int): Pair<RoomInfo, Duration> {
        val currentTime = clock.now()
        return rooms.map { room ->
            val nearestFreeInstant = room.getNearestFreeTime(minDuration)
            room to (nearestFreeInstant - currentTime)
        }.minBy { it.second }
    }

    private fun RoomInfo.getNearestFreeTime(minDuration: Int): Instant {
        val now = clock.now()
        val minGap = minDuration.minutes

        // Если нет событий и нет текущего — можно прямо сейчас
        if (currentEvent == null && eventList.isEmpty()) return now

        val firstStart = eventList.firstOrNull()?.startTime?.toInstant(timeZone)
        val currentEnd = currentEvent?.finishTime?.toInstant(timeZone)

        var nearest = currentEnd ?: now

        // Если между окончанием текущего и первым событием есть слот
        if (currentEnd != null && firstStart != null &&
            currentEnd + minGap < firstStart
        ) {
            return currentEnd
        }

        // Перебираем список событий и ищем окно между ними
        for (i in 0 until eventList.lastIndex) {
            val end = eventList[i].finishTime.toInstant(timeZone)
            val nextStart = eventList[i + 1].startTime.toInstant(timeZone)

            if (end + minGap < nextStart) {
                return end
            }

            nearest = end
        }

        return nearest
    }

    private fun RoomInfo.isFreeOn(duration: Int): Boolean {
        if (currentEvent != null) return false
        if (eventList.isEmpty()) return true

        val target = currentInstant + duration.minutes
        val firstEventStart = eventList.first().startTime.asInstant
        return target < firstEventStart
    }
}