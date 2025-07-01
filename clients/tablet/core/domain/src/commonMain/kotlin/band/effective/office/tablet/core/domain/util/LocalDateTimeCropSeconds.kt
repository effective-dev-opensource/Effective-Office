package band.effective.office.tablet.core.domain.util

import kotlinx.datetime.LocalDateTime

fun LocalDateTime.cropSeconds(): LocalDateTime =
    LocalDateTime(year, month, day, hour, minute)