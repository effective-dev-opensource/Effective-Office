package band.effective.office.shared.core.utils

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/**
 * Default system time zone.
 *
 * A getter, not a stored value, and that matters more here than anywhere else: everything that
 * decides *what time it is now* goes through this — whether a booking is the current one, how the
 * day is cut into slots, how long is left before a room frees up. Captured once at startup it keeps
 * converting in the zone the app was launched in, so after a zone change the clock still moves
 * while every one of those answers is wrong by the offset, until somebody restarts the app. That is
 * the shape the tablet was reported with: time picked up, zone not, and the countdown broken
 * afterwards.
 *
 * The lookup is cheap, and the alternative — a value that silently goes stale — is not.
 */
val defaultTimeZone: TimeZone get() = TimeZone.currentSystemDefault()

/**
 * Current local date and time in system time zone
 */
val currentLocalDateTime: LocalDateTime
    get() = Clock.System.now().toLocalDateTime(defaultTimeZone)

/**
 * Current local date in system time zone
 */
val currentLocalDate: LocalDate
    get() = Clock.System.now().toLocalDateTime(defaultTimeZone).date

/**
 * Current instant in system time zone
 */
val currentInstant: Instant
    get() = Clock.System.now()

/**
 * Rounds up the given LocalDateTime to the next 15-minute mark.
 */
fun roundUpToNextQuarter(dateTime: LocalDateTime): LocalDateTime {
    val minutes = dateTime.minute
    val remainder = minutes % 15
    val addMinutes = if (remainder == 0) 0 else 15 - remainder

    return dateTime.toInstant(defaultTimeZone)
        .plus(addMinutes.minutes)
        .toLocalDateTime(defaultTimeZone)
        .cropSeconds()
}

/**
 * Converts LocalDateTime to Instant using default time zone
 */
val LocalDateTime.asInstant: Instant
    get() = toInstant(defaultTimeZone)

/**
 * Converts Instant to LocalDateTime using default time zone
 */
val Instant.asLocalDateTime: LocalDateTime
    get() = toLocalDateTime(defaultTimeZone)

/**
 * Adds duration to LocalDateTime
 */
fun LocalDateTime.plus(duration: Duration): LocalDateTime =
    asInstant.plus(duration).asLocalDateTime

/**
 * Subtracts duration from LocalDateTime
 */
fun LocalDateTime.minus(duration: Duration): LocalDateTime =
    asInstant.minus(duration).asLocalDateTime

/**
 * Removes seconds and nanoseconds from LocalDateTime
 */
fun LocalDateTime.cropSeconds(): LocalDateTime =
    LocalDateTime(year, month, dayOfMonth, hour, minute)
