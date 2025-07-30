package band.effective.office.tablet.core.ui.utils

import kotlinx.datetime.LocalDateTime

@OptIn(kotlinx.datetime.format.FormatStringsInDatetimeFormats::class)
expect fun LocalDateTime.toLocalisedString(pattern: String): String
