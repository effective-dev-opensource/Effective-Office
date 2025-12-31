package band.effective.office.tv.feature.events.presentation.format

import androidx.compose.runtime.Composable
import band.effective.office.shared.core.utils.currentLocalDateTime
import band.effective.office.shared.core.utils.date
import band.effective.office.shared.core.utils.defaultTimeZone
import band.effective.office.shared.core.utils.time
import band.effective.office.tv.feature.events.Res
import band.effective.office.tv.feature.events.events_registration_days
import band.effective.office.tv.feature.events.events_registration_hours
import band.effective.office.tv.feature.events.events_registration_minutes
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import org.jetbrains.compose.resources.pluralStringResource
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

fun formatTimeRange(start: LocalDateTime, finish: LocalDateTime): String {
    val sameDay = start.date() == finish.date()
    val startDate = start.date()
    val startTime = start.time()
    val finishDate = finish.date()
    val finishTime = finish.time()

    return if (sameDay) {
        "$startDate, $startTime - $finishTime"
    } else {
        "$startDate, $startTime - $finishDate, $finishTime"
    }
}

@Composable
fun formatRegistrationEndsIn(endDate: LocalDateTime): String {
    val now = currentLocalDateTime
    val diff = endDate.toInstant(defaultTimeZone) - now.toInstant(defaultTimeZone)
    if (diff.isNegative()) return ""

    val days = diff.inWholeDays
    val hours = (diff - days.days).inWholeHours
    val minutes = (diff - days.days - hours.hours).inWholeMinutes
        .let { if (it == 0L) 1 else it }

    return when {
        days > 0 -> pluralStringResource(
            Res.plurals.events_registration_days,
            days.toInt(),
            days
        )

        hours > 0 -> pluralStringResource(
            Res.plurals.events_registration_hours,
            hours.toInt(),
            hours
        )

        else -> pluralStringResource(
            Res.plurals.events_registration_minutes,
            minutes.toInt(),
            minutes
        )
    }
}
