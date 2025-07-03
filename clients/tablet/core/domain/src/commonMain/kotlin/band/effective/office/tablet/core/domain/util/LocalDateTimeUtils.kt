package band.effective.office.tablet.core.domain.util

import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

val defaultTimeZone = TimeZone.currentSystemDefault()

val currentLocalDateTime: LocalDateTime get() = Clock.System.now().toLocalDateTime(defaultTimeZone)
val currentLocalDate: LocalDate get() = Clock.System.now().toLocalDateTime(defaultTimeZone).date
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

fun LocalDateTime.plus(duration: Duration) = asInstant.plus(duration).asLocalDateTime
fun LocalDateTime.minus(duration: Duration) = asInstant.minus(duration).asLocalDateTime
