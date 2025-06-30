package band.effective.office.tablet.core.domain.util

import kotlinx.datetime.LocalDateTime

internal fun LocalDateTime.cropSeconds(): LocalDateTime =
    LocalDateTime(year, month, day, hour, minute)