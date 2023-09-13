package band.effective.office.elevator.utils

import androidx.compose.ui.text.intl.Locale
import band.effective.office.elevator.domain.models.timePad
import epicarchitect.calendar.compose.basis.localized
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

fun dateFormat(date: LocalDate) =
    "${timePad(date.dayOfMonth.toString())}.${timePad(date.monthNumber.toString())}"

fun convertDateTimeToUiDateString(dateOfStart: LocalDate) =
    "${capitalizeFirstLetter(dateOfStart.dayOfWeek.localized()).lowercase()}, ${dateOfStart.dayOfMonth} ${
        MonthLocalizations.getMonthName(dateOfStart.month, Locale("ru"))
            .substring(0, 3)
            .lowercase()
    }"

fun convertDateTimeToUiDateString(dateOfStart: LocalDate, dateOfEnd: LocalDate) =
    ("${capitalizeFirstLetter(dateOfStart.dayOfWeek.localized()).lowercase()}, ${dateFormat(dateOfStart)} - " +
            "${capitalizeFirstLetter(dateOfEnd.dayOfWeek.localized()).lowercase()}, ${dateFormat(dateOfEnd)}")

fun convertTimeToString(time: LocalTime) =
    "${timePad(time.hour.toString())}:${timePad(time.minute.toString())}"