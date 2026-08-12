package band.effective.office.tablet.core.ui.utils

import kotlinx.datetime.LocalDateTime
import band.effective.office.shared.core.utils.toLocalisedString

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

    /**
     * The header line: which day is being looked at, and — while that day is today — what time it
     * is now.
     *
     * The two halves come from different places on purpose. The day is [selectDate], because the
     * arrows move it and the header has to follow them. The time is [currentDate], because
     * [selectDate] carries a time only incidentally: nothing advances it except the arrows and the
     * inactivity reset, so a header formatted from it stands still between resets and reads as a
     * clock that is a minute behind. The countdown beside it is driven by the ticker, so the two
     * numbers on screen disagreed — which is how this was noticed.
     *
     * Only while the selected day is today. Browsing another day, the time on screen would be
     * neither the selected one nor useful, and the future pattern drops it anyway.
     */
    fun map(selectDate: LocalDateTime, currentDate: LocalDateTime?): String {
        val patterns = getPatternsForLocale()
        val pattern = if (currentDate != null && selectDate.date > currentDate.date) {
            patterns.future
        } else {
            patterns.default
        }
        val shown = if (currentDate != null && selectDate.date == currentDate.date) {
            LocalDateTime(selectDate.date, currentDate.time)
        } else {
            selectDate
        }
        return shown.toLocalisedString(pattern)
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