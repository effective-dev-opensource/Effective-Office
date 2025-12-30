package band.effective.office.tablet.core.domain.util

import band.effective.office.shared.core.utils.asInstant
import band.effective.office.tablet.core.domain.model.Slot

fun Slot.freeTime(): Int {
    val startInstant = start.asInstant
    val finishInstant = finish.asInstant
    val duration = finishInstant - startInstant
    return duration.inWholeMinutes.toInt().coerceAtLeast(0)
}