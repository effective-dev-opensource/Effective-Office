package band.effective.office.backend.app.service

import band.effective.office.backend.domain.model.User
import band.effective.office.backend.repository.UserRepository
import band.effective.office.backend.repository.mapper.UserMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.UUID

/**
 * Service for managing users.
 */
@Service
class UserService(private val userRepository: UserRepository) {

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
    fun createUser(user: User): User {
        val entity = UserMapper.toEntity(user)
        val savedEntity = userRepository.save(entity)
        return UserMapper.toDomain(savedEntity)
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