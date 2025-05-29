package band.effective.office.backend.repository.mapper

import band.effective.office.backend.domain.model.User
import band.effective.office.backend.repository.entity.UserEntity
import org.springframework.stereotype.Component

/**
 * Mapper for converting between User domain model and UserEntity JPA entity.
 */
@Component
object UserMapper {
    /**
     * Converts a UserEntity to a User domain model.
     */
    fun toDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            username = entity.username,
            email = entity.email,
            firstName = entity.firstName,
            lastName = entity.lastName,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt,
            active = entity.active
        )
    }

    /**
     * Converts a User domain model to a UserEntity.
     */
    fun toEntity(domain: User): UserEntity {
        return UserEntity(
            id = domain.id,
            username = domain.username,
            email = domain.email,
            firstName = domain.firstName,
            lastName = domain.lastName,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt,
            active = domain.active
        )
    }
}