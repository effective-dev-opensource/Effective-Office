package band.effective.office.backend.feature.user.dto

import band.effective.office.backend.core.domain.model.User
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.util.UUID

/**
 * Data Transfer Object for User entity.
 */
@Schema(description = "User information")
data class UserDto(
    @Schema(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: String,

    @Schema(description = "Full name", example = "John Doe")
    val fullName: String,

    @Schema(description = "Whether the user is active", example = "true")
    val active: Boolean,

    @Schema(description = "User role", example = "USER")
    val role: String,

    @Schema(description = "URL of the user's avatar", example = "https://example.com/avatars/johndoe.png")
    val avatarUrl: String,

    @Schema(description = "User's integrations")
    val integrations: List<IntegrationDto>?,

    @Schema(description = "Email address", example = "john.doe@example.com")
    val email: String,

    @Schema(description = "User tag", example = "developer")
    val tag: String
) {
    companion object {
        /**
         * Convert a User domain model to a UserDto.
         */
        fun fromDomain(user: User): UserDto {
            return UserDto(
                id = user.id.toString(),
                fullName = "${user.firstName} ${user.lastName}",
                active = user.active,
                role = user.role,
                avatarUrl = user.avatarUrl,
                integrations = null, // TODO No integrations by default
                email = user.email,
                tag = user.tag,
            )
        }
    }
}

/**
 * Data Transfer Object for an integration.
 */
@Schema(description = "Integration information")
data class IntegrationDto(
    @Schema(description = "Integration ID", example = "123e4567-e89b-12d3-a456-426614174000")
    val id: String,

    @Schema(description = "Integration name", example = "Google")
    val name: String,

    @Schema(description = "Integration value", example = "google123")
    val value: String
)

/**
 * Data Transfer Object for creating a new User.
 */
data class CreateUserDto(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email should be valid")
    val email: String,

    @field:NotBlank(message = "Full name is required")
    val fullName: String,

    val role: String = "USER",

    val avatarUrl: String = "",

    val tag: String = ""
) {
    /**
     * Convert this CreateUserDto to a User domain model.
     */
    fun toDomain(): User {
        val nameParts = fullName.split(" ", limit = 2)
        val firstName = nameParts.getOrElse(0) { "" }
        val lastName = nameParts.getOrElse(1) { "" }

        return User(
            username = email.substringBefore("@"), // TODO Use email as username
            email = email,
            firstName = firstName,
            lastName = lastName,
            role = role,
            avatarUrl = avatarUrl,
            tag = tag
        )
    }
}

/**
 * Data Transfer Object for updating an existing User.
 */
data class UpdateUserDto(
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email should be valid")
    val email: String,

    @field:NotBlank(message = "Full name is required")
    val fullName: String,

    val role: String = "USER",

    val avatarUrl: String = "",

    val tag: String = "",

    val active: Boolean = true
) {
    /**
     * Convert this UpdateUserDto to a User domain model.
     */
    fun toDomain(id: UUID, existingUser: User): User {
        val nameParts = fullName.split(" ", limit = 2)
        val firstName = nameParts.getOrElse(0) { "" }
        val lastName = nameParts.getOrElse(1) { "" }

        return User(
            id = id,
            username = existingUser.username,
            email = email,
            firstName = firstName,
            lastName = lastName,
            createdAt = existingUser.createdAt,
            active = active,
            role = role,
            avatarUrl = avatarUrl,
            tag = tag
        )
    }
}
