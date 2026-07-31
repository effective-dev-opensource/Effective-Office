package band.effective.office.shared.core.utils

import kotlinx.datetime.LocalDateTime

/**
 * Aurora-specific implementation of toLocalisedString.
 *
 * Ни java.time, ни NSDateFormatter под linux нет, а kotlinx-datetime `byUnicodePattern`
 * отказывается разбирать locale-зависимые директивы («The directive 'MMMM' is
 * locale-dependent, but locales are not supported in Kotlin»), поэтому формат
 * раскрываем сами. Делегировать в общий `toFormattedString` нельзя — он падает, а форк
 * молча проглатывает исключение из composable и откатывает кадр, так что выглядит это
 * как «экран не отрисовался».
 *
 * Поддержаны те директивы, что реально приходят из DateDisplayMapper:
 * `d MMMM, HH:mm` / `d MMMM` / `HH:mm` (ru) и `MMM d h:mm a` / `MMM d` / `h:mm a` (en).
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

/** Порядок важен: длинные директивы должны проверяться раньше своих префиксов. */
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

/** Родительный падеж — формат «25 ноября». */
private val MONTHS_RU = listOf(
    "января", "февраля", "марта", "апреля", "мая", "июня",
    "июля", "августа", "сентября", "октября", "ноября", "декабря",
)

private val MONTHS_EN_SHORT = listOf(
    "Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec",
)
