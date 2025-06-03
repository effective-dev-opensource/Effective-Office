package band.effective.office.backend.feature.booking.core.domain.model

/**
 * Represents a recurrence pattern for a booking.
 */
data class RecurrenceModel(
    val interval: Int? = null,
    val freq: String,
    val count: Int? = null,
    val until: Long? = null,
    val byDay: List<Int> = emptyList(),
    val byMonth: List<Int> = emptyList(),
    val byYearDay: List<Int> = emptyList(),
    val byHour: List<Int> = emptyList()
)