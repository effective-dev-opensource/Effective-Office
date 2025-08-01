package band.effective.office.tablet.core.ui.utils

import kotlinx.datetime.LocalDateTime

/**
 * Data class to store date and time format patterns for a specific locale.
 *
 * @param default Format for full date and time.
 * @param future Format for future dates.
 * @param time Format for time only.
 */
private data class LocaleFormatPatterns(
    val default: String,
    val future: String,
    val time: String
)

/**
 * Utility object for formatting dates and times based on the current locale.
 * Falls back to English formats for unsupported locales.
 */
object DateDisplayMapper {

    private val formatPatterns: Map<String, LocaleFormatPatterns> = mapOf(
        "ru" to LocaleFormatPatterns(
            default = "d MMMM, HH:mm",
            future = "d MMMM",
            time = "HH:mm"
        ),
        "en" to LocaleFormatPatterns(
            default = "MMM d h:mm a",
            future = "MMM d",
            time = "h:mm a"
        )
    )
    private val defaultFormats: LocaleFormatPatterns = formatPatterns["en"]!!

    private fun getPatternsForLocale(): LocaleFormatPatterns {
        val currentLanguage = getCurrentLanguageCode()
        return formatPatterns[currentLanguage] ?: defaultFormats
    }

    fun map(selectDate: LocalDateTime, currentDate: LocalDateTime?): String {
        val patterns = getPatternsForLocale()
        val pattern = if (currentDate != null && selectDate.date > currentDate.date) {
            patterns.future
        } else {
            patterns.default
        }
        return selectDate.toLocalisedString(pattern)
    }

    fun formatForPicker(date: LocalDateTime): String {
        val patterns = getPatternsForLocale()
        return date.toLocalisedString(patterns.future)
    }

    fun formatTime(time: LocalDateTime): String {
        val patterns = getPatternsForLocale()
        return time.toLocalisedString(patterns.time)
    }

    fun is24HourFormat(): Boolean {
        return getCurrentLanguageCode() != "en"
    }
}