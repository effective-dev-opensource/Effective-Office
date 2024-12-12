package band.effective.foosball.network
data class UserResponse(
    val id: String,
    val fullName: String,
    val active: Boolean,
    val role: String,
    val avatarUrl: String,
    val integrations: List<Integration>,
    val email: String,
    val tag: String
)

data class Integration(
    val id: String,
    val name: String,
    val value: String
)