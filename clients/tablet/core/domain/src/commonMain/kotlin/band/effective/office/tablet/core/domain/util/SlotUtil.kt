package band.effective.office.tablet.core.domain.util

import band.effective.office.shared.core.utils.asInstant
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.Slot

fun Slot.freeTime(): Int {
    val startInstant = start.asInstant
    val finishInstant = finish.asInstant
    val duration = finishInstant - startInstant
    return duration.inWholeMinutes.toInt().coerceAtLeast(0)
}

/**
 * Identity of a slot that survives a rebuild of the list: the list is sliced from "now", so the
 * slots themselves are new objects every time, and the moment of slicing must not leak into the key.
 */
fun Slot.stableKey(): String = when (this) {
    // A free slot is keyed by its end, because its start is the quarter after "now" and drifts.
    is Slot.EmptySlot -> "free@$finish"
    is Slot.EventSlot -> eventInfo.stableKey()
    is Slot.LoadingEventSlot -> eventInfo.stableKey()
    is Slot.MultiEventSlot -> events.joinToString(separator = "+") { it.eventInfo.stableKey() }
}

private fun EventInfo.stableKey(): String =
    if (isNotCreated()) "pending@$startTime-$finishTime" else "event@$id"