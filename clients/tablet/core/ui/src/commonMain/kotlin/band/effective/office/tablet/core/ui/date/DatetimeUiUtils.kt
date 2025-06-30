package band.effective.office.tablet.core.ui.date

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char

val dateTimeFormat = LocalDateTime.Format {
    monthNumber(padding = Padding.SPACE)
    char('/')
    dayOfMonth(padding = Padding.ZERO)
    char(' ')
    year()
    char(' ')
    hour(padding = Padding.ZERO)
    char(':')
    minute(padding = Padding.ZERO)
}

val dayMonthFormat = LocalDateTime.Format {
    dayOfMonth(padding = Padding.ZERO)
    char(' ')
    monthName(MonthNames.ENGLISH_FULL)
}

val timeFormatter = LocalDateTime.Format {
    hour(padding = Padding.ZERO)
    char(':')
    minute(padding = Padding.ZERO)
}

fun LocalDateTime.time(): String = format(timeFormatter)

val dateFormatter = LocalDateTime.Format {
    dayOfMonth(padding = Padding.ZERO)
    char(' ')
    monthName(MonthNames.ENGLISH_FULL)
}

