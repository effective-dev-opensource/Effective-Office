package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.unbox
import band.effective.office.tablet.core.domain.util.asInstant
import band.effective.office.tablet.core.domain.util.currentInstant
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/** Timer for update when start/finish event in room */
class UpdateUseCase(
    private val timerUseCase: TimerUseCase,
    private val roomInfoUseCase: RoomInfoUseCase,
) {

    /** Flow for update when start/finish event in room */
    fun updateFlow() = flow {
        while (true) {
            val roomsInfoList = roomInfoUseCase.getCurrentRooms().unbox(
                errorHandler = { it.saveData }
            ) ?: emptyList()

            if (roomsInfoList.isNotEmpty()) {

                val timeToStartNextEvent = roomsInfoList
                    .flatMap { it.eventList }
                    .minByOrNull { it.startTime }
                    ?.let { event ->
                        val eventInstant = event.startTime.asInstant
                        eventInstant - currentInstant
                    } ?: 1.minutes

                val timeToFinishCurrentEvent = roomsInfoList
                    .mapNotNull { it.currentEvent }
                    .minByOrNull { it.startTime }
                    ?.let { event ->
                        val finishInstant = event.finishTime.asInstant
                        finishInstant - currentInstant
                    } ?: 1.minutes

                val minDelay = min(timeToStartNextEvent, timeToFinishCurrentEvent)
                val delay = if (minDelay.isNegative()) 1.minutes else minDelay

                timerUseCase.timerFlow(delay).first().apply { emit(0) }
            } else {
                timerUseCase.timerFlow(1.minutes).first().apply { emit(0) }
            }
        }
    }

    private fun min(first: Duration, second: Duration): Duration =
        if (first < second) first else second
}