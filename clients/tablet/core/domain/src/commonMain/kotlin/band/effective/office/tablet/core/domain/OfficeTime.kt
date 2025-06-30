package band.effective.office.tablet.core.domain

import kotlin.time.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object OfficeTime {

    private val startWorkLocalTime = LocalTime(8, 0)
    private val endWorkLocalTime = LocalTime(22, 0)

    private val timeZone = TimeZone.currentSystemDefault()

    fun startWorkTime(clock: Clock = Clock.System): LocalDateTime {
        val today = clock.now().toLocalDateTime(timeZone).date
        return LocalDateTime(today, startWorkLocalTime)
    }

    fun finishWorkTime(clock: Clock = Clock.System): LocalDateTime {
        val today = clock.now().toLocalDateTime(timeZone).date
        return LocalDateTime(today, endWorkLocalTime)
    }
}