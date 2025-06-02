package band.effective.office.backend.core.domain.service

import band.effective.office.backend.core.domain.model.User
import java.util.UUID

/**
 * Service interface for user operations.
 * This interface defines the contract for any user service implementation.
 */
interface UserDomainService {
    /**
     * Find a user by username.
     *
     * @param username The username to search for.
     * @return The user if found, null otherwise.
     */
    fun findByUsername(username: String): User?

    /**
     * Find a user by ID.
     *
     * @param id The user ID to search for.
     * @return The user if found, null otherwise.
     */
    fun findById(id: UUID): User?

    /**
     * Create a new user.
     *
     * @param user The user to create.
     * @return The created user.
     */
    fun createUser(user: User): User
}
