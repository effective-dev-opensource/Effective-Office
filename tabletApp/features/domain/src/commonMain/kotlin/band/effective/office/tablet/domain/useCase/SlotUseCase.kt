package band.effective.office.tablet.domain.useCase

import android.os.Build
import androidx.annotation.RequiresApi
import band.effective.office.tablet.domain.OfficeTime
import band.effective.office.tablet.domain.model.EventInfo
import band.effective.office.tablet.domain.model.Slot
import java.util.Calendar

class SlotUseCase {
    @RequiresApi(Build.VERSION_CODES.N)
    fun getSlots(
        start: Calendar = OfficeTime.startWorkTime(),
        finish: Calendar = OfficeTime.finishWorkTime(),
        minSlotDur: Int = 15,
        events: List<EventInfo>,
        currentEvent: EventInfo?
    ): List<Slot> {
        return events
            .fold(
                getEmptyMinSlots(
                    start = start,
                    finish = finish,
                    minSlotDur = minSlotDur
                )
            ) { acc, eventInfo -> acc.addEvent(eventInfo) }
            .addCurrentEvent(currentEvent)
            .mergeEmptySlots()
            .mergeEventSlot()
    }

    private fun getEmptyMinSlots(
        start: Calendar,
        finish: Calendar,
        minSlotDur: Int,
    ): List<Slot> {
        val count = (finish.timeInMillis - start.timeInMillis) / (minSlotDur * 60000)
        return List<Slot>(count.toInt()) { number ->
            val slotStart = (start.clone() as Calendar).apply { add(Calendar.MINUTE, 15 * number) }
            val slotFinish = (slotStart.clone() as Calendar).apply { add(Calendar.MINUTE, 15) }
            Slot.EmptySlot(slotStart, slotFinish)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun List<Slot>.addCurrentEvent(eventInfo: EventInfo?): List<Slot> =
        toMutableList().apply { removeEmptySlot(eventInfo) }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun List<Slot>.addEvent(eventInfo: EventInfo): List<Slot> {
        if (isEmpty()) return listOf(eventInfo.toSlot())
        val list = this.toMutableList()
        list.removeEmptySlot(eventInfo)
        val predSlot = list.firstOrNull() { it.finish > eventInfo.startTime } ?: list.first()
        val predSlotIndex = list.indexOf(predSlot)
        list.add(predSlotIndex, eventInfo.toSlot())
        return list
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun MutableList<Slot>.removeEmptySlot(eventInfo: EventInfo?) {
        if (eventInfo != null) {
            removeIf { slot ->
                slot.start > eventInfo.startTime && slot.start < eventInfo.finishTime ||
                        eventInfo.startTime > slot.start && eventInfo.startTime < slot.finish
            }
        }
    }

    private fun List<Slot>.mergeEmptySlots(): List<Slot> {
        return fold(mutableListOf<Slot>()) { acc, slot ->
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

    private fun EventInfo.toSlot(): Slot =
        Slot.EventSlot(start = startTime, finish = finishTime, eventInfo = this)
}
