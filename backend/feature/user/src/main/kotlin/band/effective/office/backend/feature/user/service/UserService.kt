package band.effective.office.backend.feature.user.service

import band.effective.office.backend.core.domain.model.User
import band.effective.office.backend.core.domain.service.UserDomainService
import band.effective.office.backend.feature.user.repository.UserRepository
import band.effective.office.backend.feature.user.repository.mapper.UserMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

/**
 * Service for managing users.
 */
@Service
class UserService(private val userRepository: UserRepository) : UserDomainService {

    /**
     * Get all users.
     *
     * @return list of all users
     */
    @Transactional(readOnly = true)
    fun getAllUsers(): List<User> {
        return userRepository.findAll().map { UserMapper.toDomain(it) }
    }

    /**
     * Get user by ID.
     *
     * @param id the user ID
     * @return the user if found, null otherwise
     */
    @Transactional(readOnly = true)
    fun getUserById(id: UUID): User? {
        return userRepository.findById(id).map { UserMapper.toDomain(it) }.orElse(null)
    }

    /**
     * Get user by username.
     *
     * @param username the username
     * @return the user if found, null otherwise
     */
    @Transactional(readOnly = true)
    fun getUserByUsername(username: String): User? {
        return userRepository.findByUsername(username)?.let { UserMapper.toDomain(it) }
    }

    /**
     * Create a new user.
     *
     * @param user the user to create
     * @return the created user
     */
    @Transactional
    override fun createUser(user: User): User {
        val entity = UserMapper.toEntity(user)
        val savedEntity = userRepository.save(entity)
        return UserMapper.toDomain(savedEntity)
    }

    /**
     * Find a user by username.
     * Implementation of UserDomainService.findByUsername.
     *
     * @param username The username to search for.
     * @return The user if found, null otherwise.
     */
    @Transactional(readOnly = true)
    override fun findByUsername(username: String): User? {
        return getUserByUsername(username)
    }

    /**
     * Find a user by ID.
     * Implementation of UserDomainService.findById.
     *
     * @param id The user ID to search for.
     * @return The user if found, null otherwise.
     */
    @Transactional(readOnly = true)
    override fun findById(id: UUID): User? {
        return getUserById(id)
    }

    /**
     * Update an existing user.
     *
     * @param id the user ID
     * @param user the updated user data
     * @return the updated user if found, null otherwise
     */
    @Transactional
    fun updateUser(id: UUID, user: User): User? {
        if (!userRepository.existsById(id)) {
            return null
        }

        val updatedUser = user.copy(
            id = id,
            updatedAt = LocalDateTime.now()
        )

        val entity = UserMapper.toEntity(updatedUser)
        val savedEntity = userRepository.save(entity)
        return UserMapper.toDomain(savedEntity)
    }

    /**
     * Delete a user by ID.
     *
     * @param id the user ID
     * @return true if the user was deleted, false otherwise
     */
    @Transactional
    fun deleteUser(id: UUID): Boolean {
        if (!userRepository.existsById(id)) {
            return false
        }

        userRepository.deleteById(id)
        return true
    }

    /**
     * Get all active users.
     *
     * @return list of active users
     */
    @Transactional(readOnly = true)
    fun getActiveUsers(): List<User> {
        return userRepository.findByActiveTrue().map { UserMapper.toDomain(it) }
    }
}
