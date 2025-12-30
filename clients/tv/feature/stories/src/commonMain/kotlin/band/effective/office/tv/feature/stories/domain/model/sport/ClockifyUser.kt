package band.effective.office.tv.feature.stories.domain.model.sport

/**
 * Domain model for sport/clockify time tracking users.
 */
data class ClockifyUser(
    val name: String,
    val email: String,
    val totalSeconds: Int,
    val photo: String? = null,
)

