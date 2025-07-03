package band.effective.office.tablet.core.domain.util

import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

val defaultTimeZone = TimeZone.currentSystemDefault()

val currentLocalDateTime: LocalDateTime get() = Clock.System.now().toLocalDateTime(defaultTimeZone)
val currentInstant: Instant get() = Instant.fromEpochMilliseconds(Clock.System.now().toEpochMilliseconds())

fun roundUpToNextQuarter(dateTime: LocalDateTime): LocalDateTime {
    val minutes = dateTime.minute
    val remainder = minutes % 15
    val addMinutes = if (remainder == 0) 0 else 15 - remainder

    return dateTime.toInstant(defaultTimeZone)
        .plus(addMinutes.minutes)
        .toLocalDateTime(defaultTimeZone)
        .cropSeconds()
}

val LocalDateTime.asInstant get() = toInstant(defaultTimeZone)
val Instant.asLocalDateTime get() = this.toLocalDateTime(defaultTimeZone)


val LocalTime.Companion.Max
    get() = LocalTime(
        hour = 23,
        minute = 59,
        second = 59,
        nanosecond = 999999999
    )

val LocalTime.Companion.Min
    get() = LocalTime(
        hour = 0,
        minute = 0,
        second = 0,
        nanosecond = 0
    )

fun LocalTime.Companion.of(
    hour: Int,
    minute: Int
) = LocalTime(hour = hour, minute = minute, second = 0, nanosecond = 0)

fun LocalTime.truncatedToMinute() = LocalTime.of(
    hour = hour,
    minute = minute
)

fun LocalTime.withHour(hour: Int) =
    LocalTime(hour = hour, minute = minute, second = second, nanosecond = nanosecond)

fun LocalTime.withMinute(minute: Int) =
    LocalTime(hour = hour, minute = minute, second = second, nanosecond = nanosecond)

fun LocalTime.isBefore(other: LocalTime) = this < other

fun LocalTime.isAfter(other: LocalTime) = this > other