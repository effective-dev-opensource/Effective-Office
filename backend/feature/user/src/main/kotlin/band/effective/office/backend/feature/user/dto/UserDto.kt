package band.effective.office.backend.feature.user.dto

import band.effective.office.backend.domain.model.User
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime
import java.util.UUID

/**
 * Data Transfer Object for User entity.
 */
data class UserDto(
    val id: UUID?,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?,
    val active: Boolean?
) {
    companion object {
        /**
         * Convert a User domain model to a UserDto.
         */
        fun fromDomain(user: User): UserDto {
            return UserDto(
                id = user.id,
                username = user.username,
                email = user.email,
                firstName = user.firstName,
                lastName = user.lastName,
                createdAt = user.createdAt,
                updatedAt = user.updatedAt,
                active = user.active
            )
        }
    }

    /**
     * Convert this UserDto to a User domain model.
     */
    fun toDomain(): User {
        return User(
            id = id ?: UUID.randomUUID(),
            username = username,
            email = email,
            firstName = firstName,
            lastName = lastName,
            createdAt = createdAt ?: LocalDateTime.now(),
            updatedAt = updatedAt ?: LocalDateTime.now(),
            active = active ?: true
        )
    }
}

/**
 * Data Transfer Object for creating a new User.
 */
data class CreateUserDto(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    val username: String,
    
    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email should be valid")
    val email: String,
    
    @field:NotBlank(message = "First name is required")
    val firstName: String,
    
    @field:NotBlank(message = "Last name is required")
    val lastName: String
) {
    /**
     * Convert this CreateUserDto to a User domain model.
     */
    fun toDomain(): User {
        return User(
            username = username,
            email = email,
            firstName = firstName,
            lastName = lastName
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
    
    @field:NotBlank(message = "First name is required")
    val firstName: String,
    
    @field:NotBlank(message = "Last name is required")
    val lastName: String,
    
    val active: Boolean?
) {
    /**
     * Convert this UpdateUserDto to a User domain model.
     */
    fun toDomain(id: UUID, existingUser: User): User {
        return User(
            id = id,
            username = existingUser.username,
            email = email,
            firstName = firstName,
            lastName = lastName,
            createdAt = existingUser.createdAt,
            active = active ?: existingUser.active
        )
    }
}