package band.effective.office.shared.core.utils

import kotlinx.datetime.LocalDateTime

/**
 * Aurora-specific implementation of toLocalisedString.
 *
 * There is neither java.time nor NSDateFormatter on linux, and kotlinx-datetime's
 * `byUnicodePattern` refuses locale-dependent directives ("The directive 'MMMM' is
 * locale-dependent, but locales are not supported in Kotlin"), so the pattern is expanded
 * here. Delegating to the common `toFormattedString` is not an option — it throws, and the
 * fork swallows an exception thrown from a composable and rolls the frame back, so it looks
 * like the screen simply failed to render.
 *
 * Covers the directives DateDisplayMapper actually produces:
 * `d MMMM, HH:mm` / `d MMMM` / `HH:mm` (ru) and `MMM d h:mm a` / `MMM d` / `h:mm a` (en).
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
    "dd" -> day.pad()
    "d" -> day.toString()
    "HH" -> hour.pad()
    "H" -> hour.toString()
    "hh" -> hour12().pad()
    "h" -> hour12().toString()
    "mm" -> minute.pad()
    "m" -> minute.toString()
    "ss" -> second.pad()
    "s" -> second.toString()
    "a" -> if (hour < 12) "AM" else "PM"
    else -> directive
}

private fun LocalDateTime.hour12(): Int = when (val h = hour % 12) {
    0 -> 12
    else -> h
}

private fun Int.pad(): String = toString().padStart(2, '0')

/** Order matters: longer directives have to be matched before their own prefixes. */
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

/** Genitive case, so that "25 ноября" reads correctly. */
private val MONTHS_RU = listOf(
    "января", "февраля", "марта", "апреля", "мая", "июня",
    "июля", "августа", "сентября", "октября", "ноября", "декабря",
)

private val MONTHS_EN_SHORT = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
)
