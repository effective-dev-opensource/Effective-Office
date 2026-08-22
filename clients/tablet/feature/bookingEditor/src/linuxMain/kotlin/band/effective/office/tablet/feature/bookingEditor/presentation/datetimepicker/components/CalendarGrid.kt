package band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.components

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus

private const val DAYS_IN_WEEK = 7
private const val WEEKS_IN_GRID = 6

/**
 * Lays the month of [month] out as [WEEKS_IN_GRID] rows of [DAYS_IN_WEEK], `null` where a row has no
 * day. Always six rows, even when five would do: a grid that changes height makes the dialog jump as
 * months are paged. Monday first, matching [WEEKDAYS_RU_SHORT].
 */
internal fun calendarMonthGrid(month: LocalDate): List<List<LocalDate?>> {
    val firstOfMonth = LocalDate(month.year, month.month, 1)
    val leadingBlanks =
        (firstOfMonth.dayOfWeek.isoDayNumber - DayOfWeek.MONDAY.isoDayNumber + DAYS_IN_WEEK) % DAYS_IN_WEEK
    val daysInMonth = firstOfMonth.plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).day

    return buildList<LocalDate?> {
        repeat(leadingBlanks) { add(null) }
        for (day in 1..daysInMonth) add(LocalDate(month.year, month.month, day))
        while (size < WEEKS_IN_GRID * DAYS_IN_WEEK) add(null)
    }.chunked(DAYS_IN_WEEK)
}
