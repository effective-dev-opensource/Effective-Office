package band.effective.office.tv.feature.stories.domain.service

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/**
 * Utility object for date parsing and celebration checks.
 */
internal object DateUtils {

    private val timeZone: TimeZone get() = TimeZone.currentSystemDefault()

    /**
     * Get current local date and time.
     */
    fun currentLocalDateTime(): LocalDateTime =
        Clock.System.now().toLocalDateTime(timeZone)

    /**
     * Get current local date.
     */
    fun currentLocalDate(): LocalDate = currentLocalDateTime().date

    /**
     * Safely parse date string in format "yyyy-MM-dd" to LocalDate.
     */
    fun String.safeToLocalDate(): LocalDate? {
        val parts = split('-')
        if (parts.size != 3) return null
        val y = parts[0].toIntOrNull() ?: return null
        val m = parts[1].toIntOrNull() ?: return null
        val d = parts[2].toIntOrNull() ?: return null
        return runCatching { LocalDate(y, m, d) }.getOrNull()
    }

    /**
     * Safely parse date string in format "yyyy-MM-dd" to LocalDateTime at midnight.
     */
    fun String.safeToLocalDateTime(): LocalDateTime? {
        val parts = split('-')
        if (parts.size != 3) return null
        val y = parts[0].toIntOrNull() ?: return null
        val m = parts[1].toIntOrNull() ?: return null
        val d = parts[2].toIntOrNull() ?: return null
        return runCatching { LocalDateTime(y, m, d, 0, 0) }.getOrNull()
    }

    /**
     * Check if today is a year celebration (birthday or anniversary) for given date.
     * Matches if month and day are the same as today.
     */
    fun isYearCelebrationToday(date: String): Boolean {
        val parsed = date.safeToLocalDate() ?: return false
        val now = currentLocalDate()
        return now.monthNumber == parsed.monthNumber && now.dayOfMonth == parsed.dayOfMonth
    }

    /**
     * Check if today is 1-month or 3-month anniversary for given date.
     * - For non-interns: checks 1-month anniversary
     * - For all: checks 3-month anniversary
     */
    fun isFirstOrThirdMonthCelebrationToday(date: String, isIntern: Boolean): Boolean {
        val parsed = date.safeToLocalDateTime() ?: return false
        val now = currentLocalDateTime()
        val oneMonthAgo = now.toInstant(timeZone)
            .minus(1, DateTimeUnit.MONTH, timeZone)
            .toLocalDateTime(timeZone)
        val threeMonthsAgo = now.toInstant(timeZone)
            .minus(3, DateTimeUnit.MONTH, timeZone)
            .toLocalDateTime(timeZone)
        return (!isIntern && oneMonthAgo.isSameDay(parsed)) || threeMonthsAgo.isSameDay(parsed)
    }

    /**
     * Check if employee is new (started within last 7 days).
     * Only applies to non-interns.
     */
    fun isNewEmployeeToday(date: String, isIntern: Boolean): Boolean {
        if (isIntern) return false
        val parsed = date.safeToLocalDateTime() ?: return false
        val end = parsed.date.plus(7, DateTimeUnit.DAY).atTime(0, 0)
        val now = currentLocalDateTime()
        return now >= parsed && now < end
    }

    /**
     * Calculate years since start date.
     */
    fun getYearsFromStartDate(date: String): Int {
        val parsed = date.safeToLocalDate() ?: return 0
        val now = currentLocalDate()
        var years = now.year - parsed.year
        if (now.monthNumber < parsed.monthNumber ||
            (now.monthNumber == parsed.monthNumber && now.dayOfMonth < parsed.dayOfMonth)
        ) {
            years--
        }
        return years
    }

    /**
     * Calculate months since start date.
     */
    fun getMonthsFromStartDate(date: String): Int {
        val parsed = date.safeToLocalDate() ?: return 0
        val now = currentLocalDate()
        val totalMonths = (now.year - parsed.year) * 12 + (now.monthNumber - parsed.monthNumber)
        return if (totalMonths < 0) 0 else totalMonths
    }

    /**
     * Check if two LocalDateTime instances are on the same day.
     */
    fun LocalDateTime.isSameDay(other: LocalDateTime): Boolean =
        year == other.year && monthNumber == other.monthNumber && dayOfMonth == other.dayOfMonth
}
