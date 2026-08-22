package band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.components

/**
 * Nominative, for a calendar header: "Ноябрь 2026". The genitive list the date formatter carries
 * ("25 ноября") does not fit here, and there are no locales on Kotlin/Native to derive either from.
 */
internal val MONTHS_RU_NOMINATIVE = listOf(
    "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь",
)

internal val WEEKDAYS_RU_SHORT = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
