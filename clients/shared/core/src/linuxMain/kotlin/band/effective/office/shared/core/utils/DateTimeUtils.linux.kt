package band.effective.office.shared.core.utils

import kotlinx.datetime.LocalDateTime

/**
 * Locale-dependent directives are rejected by `byUnicodePattern` on Kotlin/Native, so the pattern is
 * expanded here by hand. Delegating to the common [toFormattedString] throws instead of formatting.
 */
actual fun LocalDateTime.toLocalisedString(pattern: String): String {
    val result = StringBuilder(pattern.length)
    var index = 0
    while (index < pattern.length) {
        val directive = DIRECTIVES.firstOrNull { pattern.startsWith(it, index) }
        if (directive == null) {
            result.append(pattern[index])
            index++
        } else {
            result.append(render(directive))
            index += directive.length
        }
    }
    return result.toString()
}

private fun LocalDateTime.render(directive: String): String = when (directive) {
    "MMMM" -> MONTHS_RU[monthNumber - 1]
    "MMM" -> MONTHS_EN_SHORT[monthNumber - 1]
    "MM" -> monthNumber.pad()
    "M" -> monthNumber.toString()
    "yyyy" -> year.toString()
    "yy" -> (year % 100).pad()
    "dd" -> dayOfMonth.pad()
    "d" -> dayOfMonth.toString()
    "HH" -> hour.pad()
    "H" -> hour.toString()
    "hh" -> hour12().pad()
    "h" -> hour12().toString()
    "mm" -> minute.pad()
    "m" -> minute.toString()
    "ss" -> second.pad()
    "s" -> second.toString()
    "a" -> if (hour < NOON_HOUR) "AM" else "PM"
    else -> directive
}

private fun LocalDateTime.hour12(): Int = when (val hourOfHalfDay = hour % NOON_HOUR) {
    0 -> NOON_HOUR
    else -> hourOfHalfDay
}

private fun Int.pad(): String = toString().padStart(PAD_WIDTH, '0')

private const val NOON_HOUR = 12
private const val PAD_WIDTH = 2

/** Longer directives have to be tried before the shorter ones they start with. */
private val DIRECTIVES = listOf(
    "MMMM", "MMM", "MM", "M",
    "yyyy", "yy",
    "dd", "d",
    "HH", "H",
    "hh", "h",
    "mm", "m",
    "ss", "s",
    "a",
)

/** Genitive case: the pattern reads "25 ноября", never "25 ноябрь". */
private val MONTHS_RU = listOf(
    "января", "февраля", "марта", "апреля", "мая", "июня",
    "июля", "августа", "сентября", "октября", "ноября", "декабря",
)

private val MONTHS_EN_SHORT = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
)
