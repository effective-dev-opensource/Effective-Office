package office.effective.features.booking.converters

import model.Ending
import model.Freq
import model.Recurrence
import java.lang.Exception

/**
 * Object for creating Google calendar recurrence rule
 *
 * @author Max Mishenko
 */
object RecurrenceRuleFactory {

    /**
     * @author Max Mishenko
     */
    fun String.getRecurrence(): Recurrence = trim('[', ']').substringAfter(":").split(";").fold(
        Recurrence(
            interval = 0,
            freq = Freq.DAILY,
            ending = Ending.Empty,
            byDay = listOf(),
            byMonth = listOf(),
            byYearDay = listOf(),
            byHour = listOf()
        )
    ) { recurrence, param ->
        val parts = param.split("=")
        when (parts.first()) {
            "FREQ" -> recurrence.copy(freq = Freq.valueOf(parts.last()))
            "INTERVAL" -> recurrence.copy(interval = parts.last().toInt())
            "COUNT" -> recurrence.copy(ending = Ending.Count(parts.last().toInt()))
            "UNTIL" -> recurrence.copy(
                ending = Ending.Until(
                    parts.last()
                )
            )

            "BYDAY" -> recurrence.copy(byDay = parts.last().split(",").map { it.toDayNum() })
            "BYMONTH" -> recurrence.copy(byMonth = parts.last().split(",").map { it.toInt() })
            "BYYEARDAY" -> recurrence.copy(byYearDay = parts.last().split(",").map { it.toInt() })
            "BYHOUR" -> recurrence.copy(byHour = parts.last().split(",").map { it.toInt() })
            else -> recurrence
        }
    }

    /**
     * @author Max Mishenko
     */
    fun Recurrence.rule() =
        "RRULE:".let { it + "FREQ=${freq.name};" }.let { if (interval != 0) it + "INTERVAL=${interval};" else it }.let {
            it + when (ending) {
                is Ending.Count -> "COUNT=${ending.value};"
                is Ending.Until -> "UNTIL=${ending.value};"
                else -> ""
            }
        }.let {
            if (byDay.isNotEmpty()) it + byDay.fold("BYDAY=") { sum, element -> "$sum${element.toName()}," }
                .trim(',') + ";" else it
        }.let {
            if (byMonth.isNotEmpty()) it + byMonth.fold("BYMONTH=") { sum, element -> "$sum$element," }
                .trim(',') + ";" else it
        }.let {
            if (byYearDay.isNotEmpty()) it + byYearDay.fold("BYYEARDAY=") { sum, element -> "$sum$element," }
                .trim(',') + ";" else it
        }.let {
            if (byHour.isNotEmpty()) it + byHour.fold("BYHOUR=") { sum, element -> "$sum$element," }
                .trim(',') + ";" else it
        }

    /**
     * @author Max Mishenko
     */
    private fun Int.toName(): String = when (this) {
        1 -> "MO"
        2 -> "TU"
        3 -> "WE"
        4 -> "TH"
        5 -> "FR"
        6 -> "SA"
        7 -> "SU"
        else -> ""
    }

    /**
     * @author Max Mishenko
     */
    private fun String.toDayNum(): Int = when (this) {
        "MO" -> 1
        "TU" -> 2
        "WE" -> 3
        "TH" -> 4
        "FR" -> 5
        "SA" -> 6
        "SU" -> 7
        else -> 0
    }
}