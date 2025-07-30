package band.effective.office.tablet.core.ui.utils


import kotlinx.datetime.LocalDateTime

object DateDisplayMapper {

    private const val FUTURE_DATE_FORMAT = "d MMMM"
    private const val DEFAULT_DATE_FORMAT = "HH:mm, d MMMM"

    fun map(selectDate: LocalDateTime, currentDate: LocalDateTime?): String {
        val isFutureDate = currentDate != null && selectDate.date > currentDate.date
        val pattern = if (isFutureDate) FUTURE_DATE_FORMAT else DEFAULT_DATE_FORMAT
        return selectDate.toLocalisedString(pattern)
    }

    fun formatForPicker(date: LocalDateTime): String {
        return date.toLocalisedString(FUTURE_DATE_FORMAT)
    }
}