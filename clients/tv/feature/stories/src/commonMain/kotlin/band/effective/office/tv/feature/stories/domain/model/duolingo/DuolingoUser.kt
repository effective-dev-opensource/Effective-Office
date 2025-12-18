package band.effective.office.tv.feature.stories.domain.model

/**
 * Domain model for Duolingo users.
 * Note: photo подтягивается из teammates.
 */
data class DuolingoUser(
    val username: String,
    val name: String?,
    val streak: Int,
    val totalXp: Int,
    val countryLang: List<String>,
    val photo: String? = null,
)

