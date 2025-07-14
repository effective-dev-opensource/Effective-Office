package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.util.asInstant
import band.effective.office.tablet.core.domain.util.currentInstant
import io.github.aakira.napier.Napier
import kotlin.math.absoluteValue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

// TODO rename
open class SelectRoomUseCase(
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
    private val clock: Clock = Clock.System
) {

    open fun getRoom(currentRoom: RoomInfo, rooms: List<RoomInfo>, minEventDuration: Int): RoomInfo? {
        Napier.d { "[SelectRoomUseCase] Selecting room, currentRoom=${currentRoom.name}, minDuration=$minEventDuration minutes" }
        val candidates = rooms.filter { it.isFreeOn(minEventDuration) }
            .sortedBy { (it.capacity - currentRoom.capacity).absoluteValue }
        return if (candidates.contains(currentRoom)) currentRoom else candidates.firstOrNull()
    }

    fun getNearestFreeRoom(rooms: List<RoomInfo>, minDuration: Int): Pair<RoomInfo, Duration> {
        val currentTime = clock.now()
        return rooms.map { room ->
            val nearestFreeInstant = room.getNearestFreeTime(minDuration)
            room to (nearestFreeInstant - currentTime)
        }.minBy { it.second }.also { result ->
            Napier.d { "[SelectRoomUseCase] Finding nearest free room for minDuration=$minDuration minutes, selected: ${result.first.name}, free in ${result.second}" }
        }
    }

    private fun RoomInfo.getNearestFreeTime(minDuration: Int): Instant {
        val now = clock.now()
        val minGap = minDuration.minutes

        // Если нет событий и нет текущего — можно прямо сейчас
        if (currentEvent == null && eventList.isEmpty()) return now.also {
            Napier.d { "[SelectRoomUseCase] Room ${name} is free now" }
        }
        val firstStart = eventList.firstOrNull()?.startTime?.toInstant(timeZone)
        val currentEnd = currentEvent?.finishTime?.toInstant(timeZone)

        var nearest = currentEnd ?: now

        // Если между окончанием текущего и первым событием есть слот
        if (currentEnd != null && firstStart != null &&
            currentEnd + minGap < firstStart
        ) {
            return currentEnd.also {
                Napier.d { "[SelectRoomUseCase] Found free slot for room=${name} after current event ends at $currentEnd" }
            }
        }

        // Перебираем список событий и ищем окно между ними
        for (i in 0 until eventList.lastIndex) {
            val end = eventList[i].finishTime.toInstant(timeZone)
            val nextStart = eventList[i + 1].startTime.toInstant(timeZone)

            if (end + minGap < nextStart) {
                return end.also {
                    Napier.d { "[SelectRoomUseCase] Found free slot for room=${name} between events at $end" }
                }
            }

            nearest = end
        }

        return nearest.also {
            Napier.d { "[SelectRoomUseCase] Nearest free time for room=${name} is $it" }
        }
    }

    private fun RoomInfo.isFreeOn(duration: Int): Boolean {
        if (currentEvent != null) return false.also {
            Napier.d { "[SelectRoomUseCase] Room ${name} is not free due to current event" }
        }
        if (eventList.isEmpty()) return true.also {
            Napier.d { "[SelectRoomUseCase] Room ${name} is free" }
        }

        val target = currentInstant + duration.minutes
        val firstEventStart = eventList.minByOrNull { it.startTime }!!.startTime.asInstant
        return target < firstEventStart
    }
}