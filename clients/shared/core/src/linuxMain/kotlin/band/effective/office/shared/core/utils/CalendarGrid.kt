package band.effective.office.shared.core.utils

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus

private const val DAYS_IN_WEEK = 7

/**
 * Six rows always, padded with nulls — a month needs five or six depending on where it starts, and
 * a grid that changes height makes the dialog jump every time you page a month.
 */
private const val WEEKS = 6

/** A month laid out for display: [weeks] is always 6 rows of 7, `null` where there is no day. */
data class CalendarMonthGrid(
    val year: Int,
    val month: Int,
    val weeks: List<List<LocalDate?>>,
)

/**
 * Lays a month out for a calendar, without asking the platform anything.
 *
 * This exists because the Aurora fork's Material3 cannot do it: its `PlatformDateFormat` is a stub
 * with `firstDayOfWeek = 0` and no weekday names, and `DatePicker` walks
 * `firstDayOfWeek - 1 until weekdayNames.size` — which on an empty list indexes -1 and throws on
 * the first frame.
 */
fun calendarMonthGrid(
    year: Int,
    month: Int,
    firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
): CalendarMonthGrid {
    val firstOfMonth = LocalDate(year, month, 1)
    val leadingBlanks =
        ((firstOfMonth.dayOfWeek.isoDayNumber - firstDayOfWeek.isoDayNumber) + DAYS_IN_WEEK) % DAYS_IN_WEEK

    val cells = buildList<LocalDate?> {
        repeat(leadingBlanks) { add(null) }
        for (day in 1..firstOfMonth.lengthOfMonth()) add(LocalDate(year, month, day))
        while (size < WEEKS * DAYS_IN_WEEK) add(null)
    }

    return CalendarMonthGrid(year = year, month = month, weeks = cells.chunked(DAYS_IN_WEEK))
}

/**
 * Adds [months] to this date, clamping the day to the target month: 31 January plus one month is
 * 28 or 29 February, not an invalid date.
 */
fun LocalDate.plusMonths(months: Int): LocalDate {
    val target = LocalDate(year, month, 1).plus(months, DateTimeUnit.MONTH)
    return LocalDate(target.year, target.month, day.coerceAtMost(target.lengthOfMonth()))
}

private fun LocalDate.lengthOfMonth(): Int =
    LocalDate(year, month, 1).plus(1, DateTimeUnit.MONTH).minus(1, DateTimeUnit.DAY).day
