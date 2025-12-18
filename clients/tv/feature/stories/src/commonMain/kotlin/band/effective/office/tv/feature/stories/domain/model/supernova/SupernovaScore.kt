package band.effective.office.tv.feature.stories.domain.model.supernova

/**
 * Domain model for Supernova talent scores.
 */
data class SupernovaScore(
    val id: String,
    val score: Int,
    val name: String? = null,
    val photo: String? = null,
)

