package band.effective.office.backend.feature.user.repository

import band.effective.office.backend.feature.user.repository.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

/**
 * Repository for accessing User entities in the database.
 */
@Repository
interface UserRepository : JpaRepository<UserEntity, UUID> {
    /**
     * Find a user by username.
     *
     * @param username the username to search for
     * @return the user entity if found, null otherwise
     */
    fun findByUsername(username: String): UserEntity?

    /**
     * Find a user by email.
     *
     * @param email the email to search for
     * @return the user entity if found, null otherwise
     */
    fun findByEmail(email: String): UserEntity?

    /**
     * Find all active users.
     *
     * @return a list of active user entities
     */
    fun findByActiveTrue(): List<UserEntity>
}