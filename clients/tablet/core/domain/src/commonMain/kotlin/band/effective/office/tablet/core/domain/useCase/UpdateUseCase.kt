package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.unbox
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

/** Timer for update when start/finish event in room */
class UpdateUseCase(
    private val timerUseCase: TimerUseCase,
    private val roomInfoUseCase: RoomInfoUseCase,
) {
    private val timeZone: TimeZone = TimeZone.currentSystemDefault()
    private val clock: Clock = Clock.System

    /** Flow for update when start/finish event in room */
    fun updateFlow() = flow {
        while (true) {
            val roomInfo = roomInfoUseCase.getCurrentRooms().unbox(
                errorHandler = { it.saveData }
            ) ?: emptyList()

            if (roomInfo.isNotEmpty()) {
                val now = clock.now()

                val timeToStartNextEvent = roomInfo
                    .flatMap { it.eventList }
                    .minByOrNull { it.startTime }
                    ?.let { event ->
                        val eventInstant = event.startTime.toInstant(timeZone)
                        (eventInstant - now).coerceAtLeast(Duration.ZERO)
                    } ?: 1.minutes

                val timeToFinishCurrentEvent = roomInfo
                    .mapNotNull { it.currentEvent }
                    .minByOrNull { it.startTime }
                    ?.let { event ->
                        val finishInstant = event.finishTime.toInstant(timeZone)
                        (finishInstant - now).coerceAtLeast(Duration.ZERO)
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