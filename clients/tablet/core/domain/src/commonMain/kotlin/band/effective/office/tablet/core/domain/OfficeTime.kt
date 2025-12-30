package band.effective.office.tablet.core.domain

import band.effective.office.shared.core.utils.currentLocalDateTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

object OfficeTime {

    private val startWorkLocalTime = LocalTime(8, 0)
    private val endWorkLocalTime = LocalTime(22, 0)

    fun startWorkTime(localDate: LocalDate = currentLocalDateTime.date): LocalDateTime =
        LocalDateTime(localDate, startWorkLocalTime)

    fun finishWorkTime(localDate: LocalDate = currentLocalDateTime.date): LocalDateTime =
        LocalDateTime(localDate, endWorkLocalTime)

}