package band.effective.office.tablet.core.domain.model

import band.effective.office.shared.core.utils.asInstant
import kotlinx.datetime.LocalDateTime

sealed class Slot {
    abstract val start: LocalDateTime
    abstract val finish: LocalDateTime

    fun minuteDuration(): Int {
        val startMillis = start.asInstant.toEpochMilliseconds()
        val finishMillis = finish.asInstant.toEpochMilliseconds()
        return ((finishMillis - startMillis) / 60000).toInt()
    }

    data class EmptySlot(
        override val start: LocalDateTime,
        override val finish: LocalDateTime,
    ) : Slot()

    data class EventSlot(
        override val start: LocalDateTime,
        override val finish: LocalDateTime,
        val eventInfo: EventInfo
    ) : Slot()

    data class MultiEventSlot(
        override val start: LocalDateTime,
        override val finish: LocalDateTime,
        val events: List<EventSlot>
    ) : Slot()

    data class LoadingEventSlot(
        override val start: LocalDateTime,
        override val finish: LocalDateTime,
        val eventInfo: EventInfo
    ) : Slot()
}