package band.effective.office.shared.core.utils

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.format.char

/**
 * Date and time formatting utilities for kotlinx.datetime
 */

/**
 * Format: "HH:mm DD Month"
 * Example: "14:30 25 November"
 */
val dateTimeFormat = LocalDateTime.Format {
    hour(padding = Padding.ZERO)
    char(':')
    minute(padding = Padding.ZERO)
    char(' ')
    dayOfMonth(padding = Padding.ZERO)
    char(' ')
    monthName(MonthNames.ENGLISH_FULL)
}

/**
 * Format: "DD Month"
 * Example: "25 November"
 */
val dayMonthFormat = LocalDateTime.Format {
    dayOfMonth(padding = Padding.ZERO)
    char(' ')
    monthName(MonthNames.ENGLISH_FULL)
}

/**
 * Format: "HH:mm"
 * Example: "14:30"
 */
val timeFormatter = LocalDateTime.Format {
    hour(padding = Padding.ZERO)
    char(':')
    minute(padding = Padding.ZERO)
}

/**
 * Extension function to format LocalDateTime as time string
 * @return Time string in "HH:mm" format
 */
fun LocalDateTime.time(): String = format(timeFormatter)

/**
 * Format: "DD Month"
 * Example: "25 November"
 */
val dateFormatter = LocalDateTime.Format {
    dayOfMonth(padding = Padding.ZERO)
    char(' ')
    monthName(MonthNames.ENGLISH_FULL)
}

/**
 * Extension function to format LocalDateTime as date string
 * @return Date string in "DD Month" format
 */
fun LocalDateTime.date(): String = format(dateFormatter)

/**
 * Formats LocalDateTime using a custom Unicode pattern
 * @param pattern Unicode date/time pattern (e.g., "HH:mm", "dd.MM.yyyy")
 * @return Formatted string
 */
@OptIn(FormatStringsInDatetimeFormats::class)
fun LocalDateTime.toFormattedString(pattern: String): String {
    val formatter = LocalDateTime.Format {
        byUnicodePattern(pattern)
    }
    return formatter.format(this)
}

/**
 * Formats LocalTime using a custom Unicode pattern
 * @param pattern Unicode time pattern (e.g., "HH:mm:ss")
 * @return Formatted string
 */
@OptIn(FormatStringsInDatetimeFormats::class)
fun LocalTime.toFormattedString(pattern: String): String {
    val formatter = LocalTime.Format {
        byUnicodePattern(pattern)
    }
    return formatter.format(this)
}
