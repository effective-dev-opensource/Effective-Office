package band.effective.office.tablet.core.domain.model

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

sealed class Slot {
    abstract val start: LocalDateTime
    abstract val finish: LocalDateTime

    fun minuteDuration(): Int {
        val startMillis = start.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        val finishMillis = finish.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
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