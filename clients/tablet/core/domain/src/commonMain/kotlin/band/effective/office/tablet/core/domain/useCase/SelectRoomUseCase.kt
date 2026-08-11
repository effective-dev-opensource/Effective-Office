package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.shared.core.utils.asInstant
import band.effective.office.shared.core.utils.currentInstant
import band.effective.office.shared.core.utils.defaultTimeZone
import kotlin.math.absoluteValue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant

// TODO rename
open class SelectRoomUseCase(
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
        // Taken once: every conversion below belongs to the same answer, and a zone read again
        // halfway through could be a different one.
        val timeZone = defaultTimeZone
        val minGap = minDuration.minutes

        // No events at all and nothing running — free right now
        if (currentEvent == null && eventList.isEmpty()) return now

        val firstStart = eventList.firstOrNull()?.startTime?.toInstant(timeZone)
        val currentEnd = currentEvent?.finishTime?.toInstant(timeZone)

        var nearest = currentEnd ?: now

        // A slot between the end of the current event and the first upcoming one
        if (currentEnd != null && firstStart != null &&
            currentEnd + minGap < firstStart
        ) {
            return currentEnd
        }

        // Walk the event list looking for a gap between consecutive events
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
        val firstEventStart = eventList.minByOrNull { it.startTime }!!.startTime.asInstant
        return target < firstEventStart
    }
}