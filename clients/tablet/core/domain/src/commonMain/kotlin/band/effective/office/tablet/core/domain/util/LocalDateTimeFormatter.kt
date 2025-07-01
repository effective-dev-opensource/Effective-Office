package band.effective.office.tablet.core.domain.util

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern

private const val formatPattern = "HH:mm"

@OptIn(FormatStringsInDatetimeFormats::class)
        /**
         * Format time as 09:07
         */
val timeFormatter = LocalDateTime.Format {
    byUnicodePattern(formatPattern)
}

@OptIn(FormatStringsInDatetimeFormats::class)
fun LocalDateTime.toFormattedString(pattern: String): String {
    val formatter = LocalDateTime.Format {
        byUnicodePattern(pattern)
    }
    return formatter.format(this)
}
