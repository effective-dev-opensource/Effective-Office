package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.OfficeTime
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.Slot
import band.effective.office.tablet.core.domain.util.cropSeconds
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class SlotUseCase(
    private val timeZone: TimeZone = TimeZone.currentSystemDefault()
) {
    fun getSlots(
        start: LocalDateTime = OfficeTime.startWorkTime(),
        finish: LocalDateTime = OfficeTime.finishWorkTime(),
        minSlotDur: Int = 15,
        events: List<EventInfo>,
        currentEvent: EventInfo?,
    ): List<Slot> {
        return events
            .filter { it.startTime >= start && it.startTime < finish && it.finishTime <= finish }
            .fold(
                getEmptyMinSlots(start, finish, minSlotDur)
            ) { acc, eventInfo -> acc.addEvent(eventInfo) }
            .addCurrentEvent(currentEvent)
            .mergeEmptySlots()
            .mergeEventSlot()
    }

    private fun getEmptyMinSlots(
        start: LocalDateTime,
        finish: LocalDateTime,
        minSlotDur: Int,
    ): List<Slot> {
        if (start >= finish) return emptyList()

        val durationMinutes = finish.toInstant(timeZone).toEpochMilliseconds() -
                start.toInstant(timeZone).toEpochMilliseconds()
        val count = durationMinutes / (minSlotDur * 60 * 1000)

        return List(count.toInt()) { number ->
            val slotStart = start
                .toInstant(timeZone)
                .plus(number * minSlotDur, DateTimeUnit.MINUTE)
                .toLocalDateTime(timeZone)
            val slotFinish = slotStart
                .toInstant(timeZone).plus(minSlotDur, DateTimeUnit.MINUTE)
                .toLocalDateTime(timeZone)
            Slot.EmptySlot(slotStart, slotFinish)
        }
    }

    private fun List<Slot>.addCurrentEvent(eventInfo: EventInfo?): List<Slot> =
        toMutableList().apply { removeEmptySlot(eventInfo) }

    private fun List<Slot>.addEvent(eventInfo: EventInfo): List<Slot> {
        if (isEmpty()) return listOf(eventInfo.toSlot())
        val list = this.toMutableList()
        list.removeEmptySlot(eventInfo)
        val predSlotIndex = list.indexOfFirst { it.start >= eventInfo.startTime }
        if (predSlotIndex != -1) {
            list.add(predSlotIndex, eventInfo.toSlot())
        } else {
            list.add(eventInfo.toSlot())
        }
        return list
    }

    private fun MutableList<Slot>.removeEmptySlot(eventInfo: EventInfo?) {
        if (eventInfo == null) return

        val eventEndWithoutSeconds = eventInfo.finishTime.cropSeconds()

        removeAll { slot ->
            val firstCondition = slot.start >= eventInfo.startTime && slot.start < eventEndWithoutSeconds
            val secondCondition = eventInfo.startTime >= slot.start && eventInfo.startTime < slot.finish
            firstCondition || secondCondition
        }
    }

    private fun List<Slot>.mergeEmptySlots(): List<Slot> {
        return fold(mutableListOf()) { acc, slot ->
            if (acc.isEmpty()) {
                (acc + slot).toMutableList()
            } else {
                val lastSlot = acc.last()
                if (lastSlot is Slot.EmptySlot && slot is Slot.EmptySlot) {
                    (acc.dropLast(1) + Slot.EmptySlot(lastSlot.start, slot.finish)).toMutableList()
                } else {
                    (acc + slot).toMutableList()
                }
            }
        }
    }

    private fun List<Slot>.mergeEventSlot(): List<Slot> {
        return fold(mutableListOf()) { acc, slot ->
            val lastSlot = acc.lastOrNull()
            when {
                lastSlot == null -> (acc + slot).toMutableList()
                lastSlot is Slot.EventSlot && slot is Slot.EventSlot ->
                    (acc.dropLast(1) + Slot.MultiEventSlot(
                        start = lastSlot.start,
                        finish = slot.finish,
                        events = listOf(lastSlot, slot)
                    )).toMutableList()

                lastSlot is Slot.MultiEventSlot && slot is Slot.EventSlot -> (acc.dropLast(1) + Slot.MultiEventSlot(
                    start = lastSlot.start,
                    finish = slot.finish,
                    events = lastSlot.events + slot
                )).toMutableList()

                else -> (acc + slot).toMutableList()
            }
        }
    }

    private fun EventInfo.toSlot(): Slot {
        return if (!isLoading)
            Slot.EventSlot(start = startTime, finish = finishTime, eventInfo = this)
        else Slot.LoadingEventSlot(start = startTime, finish = finishTime, eventInfo = this)
    }
}