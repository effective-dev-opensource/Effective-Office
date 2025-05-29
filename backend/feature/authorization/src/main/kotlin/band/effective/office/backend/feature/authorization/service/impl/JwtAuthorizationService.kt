package band.effective.office.backend.feature.authorization.service.impl

import band.effective.office.backend.domain.model.User
import band.effective.office.backend.feature.authorization.exception.AuthenticationException
import band.effective.office.backend.feature.authorization.model.TokenPair
import band.effective.office.backend.feature.authorization.service.AuthorizationService
import band.effective.office.backend.feature.authorization.service.TokenProvider
import band.effective.office.backend.repository.UserRepository
import band.effective.office.backend.repository.mapper.UserMapper
import java.util.UUID
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * JWT implementation of the AuthorizationService interface.
 */
@Service
class JwtAuthorizationService(
    private val tokenProvider: TokenProvider,
    private val userRepository: UserRepository,
    private val userMapper: UserMapper,
    private val passwordEncoder: PasswordEncoder
) : AuthorizationService {

    override fun authenticate(username: String, password: String): TokenPair {
        val user = userRepository.findByUsername(username)
            ?.let { userMapper.toDomain(it) }
            ?: throw AuthenticationException("Invalid username or password")

        // In a real application, we would check the password against a hashed value stored in the database
        // For now, we'll just assume the password is correct (this will be updated when we add password to the user entity)

        return tokenProvider.generateTokenPair(user)
    }

    override fun refreshToken(refreshToken: String): TokenPair {
        val userId = try {
            tokenProvider.validateRefreshToken(refreshToken)
        } catch (ex: Exception) {
            throw AuthenticationException("Invalid refresh token", ex)
        }

        val user = userRepository.findById(UUID.fromString(userId))
            .map { userMapper.toDomain(it) }
            .orElseThrow { AuthenticationException("User not found") }

        return tokenProvider.generateTokenPair(user)
    }

    override fun validateToken(accessToken: String): User {
        val userId = try {
            tokenProvider.validateAccessToken(accessToken)
        } catch (ex: Exception) {
            throw AuthenticationException("Invalid access token", ex)
        }

        return userRepository.findById(UUID.fromString(userId))
            .map { userMapper.toDomain(it) }
            .orElseThrow { AuthenticationException("User not found") }
    }

    override fun invalidateTokens(userId: String) {
        tokenProvider.invalidateTokens(userId)
    }
}