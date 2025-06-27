package band.effective.office.client.core.data.dto.user

import kotlinx.serialization.Serializable

/**
 * Represents a user in the system.
 * @property id Unique identifier (UUIDv4)
 * @property fullName Full name of the user
 * @property active Whether the user is active
 * @property role Role of the user
 * @property avatarUrl URL to the user's avatar
 * @property integrations List of integrations associated with the user
 * @property email Email address of the user
 * @property tag User type (employee or guest)
 */
@Serializable
data class UserDTO(
    val id: String,
    val fullName: String,
    val active: Boolean,
    val role: String,
    val avatarUrl: String,
    val integrations: List<IntegrationDTO>? = null,
    val email: String = "",
    val tag: String = ""
)