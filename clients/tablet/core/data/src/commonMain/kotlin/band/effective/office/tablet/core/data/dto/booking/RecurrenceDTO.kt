package band.effective.office.tablet.core.data.dto.booking

import kotlinx.serialization.Serializable

/**
 * Represents a recurrence pattern for bookings.
 * @property interval Period of bookings (e.g., every 2 days)
 * @property freq Frequency type (DAILY, WEEKLY, MONTHLY, YEARLY)
 * @property count Number of occurrences
 * @property until End date of recurrence
 * @property byDay Days of the week for recurrence
 * @property byMonth Months for recurrence
 * @property byYearDay Days of the year for recurrence
 * @property byHour Hours for recurrence
 */
@Serializable
data class RecurrenceDTO(
    val interval: Int? = null,
    val freq: String,
    val count: Int? = null,
    val until: Long? = null,
    val byDay: List<Int> = listOf(),
    val byMonth: List<Int> = listOf(),
    val byYearDay: List<Int> = listOf(),
    val byHour: List<Int> = listOf()
)