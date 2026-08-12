package band.effective.office.shared.core.utils

/**
 * Russian calendar strings, hand-written because there are no locales on Kotlin/Native and the
 * Aurora fork's Material3 ships an empty stub in their place.
 *
 * Two month lists, and they are not interchangeable. Formatting a date needs the genitive —
 * "25 ноября" — while a calendar header needs the nominative — "Ноябрь 2026".
 */

/** Genitive, for "25 ноября". Used by the date formatter. */
internal val MONTHS_RU_GENITIVE = listOf(
    "января", "февраля", "марта", "апреля", "мая", "июня",
    "июля", "августа", "сентября", "октября", "ноября", "декабря",
)

/** Nominative, for a calendar header: "Ноябрь 2026". */
val MONTHS_RU_NOMINATIVE = listOf(
    "Январь", "Февраль", "Март", "Апрель", "Май", "Июнь",
    "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь",
)

/** Monday first, matching [calendarMonthGrid]'s default. */
val WEEKDAYS_RU_SHORT = listOf("Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс")
