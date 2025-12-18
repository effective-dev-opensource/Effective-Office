package band.effective.office.tv.feature.stories.domain.model.teammates

/**
 * Teammate domain model: shared person info used to enrich stories.
 */
data class Teammate(
    val id: String,
    val name: String,
    val startDate: String,
    val nextBDay: String,
    val photo: String,
    val employment: String,
    val duolingo: String?,
    val email: String? = null,
)

