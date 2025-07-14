package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.unbox
import band.effective.office.tablet.core.domain.util.asInstant
import band.effective.office.tablet.core.domain.util.currentInstant
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/** Timer for update when start/finish event in room */
class UpdateUseCase(
    private val timerUseCase: TimerUseCase,
    private val roomInfoUseCase: RoomInfoUseCase,
) {
    companion object {
        /** Default delay duration when no events are scheduled or when calculated delay is invalid */
        private val DEFAULT_DELAY = 1.minutes
    }

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
                    } ?: DEFAULT_DELAY

                val timeToFinishCurrentEvent = roomsInfoList
                    .mapNotNull { it.currentEvent }
                    .minByOrNull { it.startTime }
                    ?.let { event ->
                        val finishInstant = event.finishTime.asInstant
                        finishInstant - currentInstant
                    } ?: DEFAULT_DELAY

                val minDelay = min(timeToStartNextEvent, timeToFinishCurrentEvent)
                // Ensure delay is always positive to prevent too frequent updates
                val delay = if (minDelay.isNegative() || minDelay.inWholeMilliseconds == 0L) DEFAULT_DELAY else minDelay

                timerUseCase.timerFlow(delay).first().apply { emit(0) }
            } else {
                timerUseCase.timerFlow(DEFAULT_DELAY).first().apply { emit(0) }
            }
        }
    }

    private fun min(first: Duration, second: Duration): Duration =
        if (first < second) first else second
}
