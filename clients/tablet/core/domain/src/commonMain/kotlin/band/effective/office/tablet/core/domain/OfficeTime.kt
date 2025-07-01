package band.effective.office.tablet.core.domain

import band.effective.office.tablet.core.domain.util.currentLocalDateTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

object OfficeTime {

    private val startWorkLocalTime = LocalTime(8, 0)
    private val endWorkLocalTime = LocalTime(22, 0)

    fun startWorkTime(localDateTime: LocalDateTime = currentLocalDateTime): LocalDateTime =
        LocalDateTime(localDateTime.date, startWorkLocalTime)

    fun finishWorkTime(localDateTime: LocalDateTime = currentLocalDateTime): LocalDateTime =
        LocalDateTime(localDateTime.date, endWorkLocalTime)

}