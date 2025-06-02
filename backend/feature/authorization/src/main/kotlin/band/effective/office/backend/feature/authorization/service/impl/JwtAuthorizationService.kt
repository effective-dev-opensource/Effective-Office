package band.effective.office.backend.feature.authorization.service.impl

import band.effective.office.backend.core.domain.model.User
import band.effective.office.backend.core.domain.service.UserDomainService
import band.effective.office.backend.feature.authorization.exception.AuthenticationException
import band.effective.office.backend.feature.authorization.model.TokenPair
import band.effective.office.backend.feature.authorization.service.AuthorizationService
import band.effective.office.backend.feature.authorization.service.TokenProvider
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import java.util.Collections
import java.util.UUID
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * JWT implementation of the AuthorizationService interface.
 */
@Service
class JwtAuthorizationService(
    private val tokenProvider: TokenProvider,
    private val userService: UserDomainService,
    @Value("\${google.client-id}") private val googleClientId: String
) : AuthorizationService {

    private val verifier = GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
        .setAudience(Collections.singletonList(googleClientId))
        .build()

    override fun authenticate(username: String, password: String): TokenPair {
        val user = userService.findByUsername(username)
            ?: throw AuthenticationException("Invalid username or password")

        // In a real application, we would check the password against a hashed value stored in the database
        // For now, we'll just assume the password is correct (this will be updated when we add password to the user entity)

        return tokenProvider.generateTokenPair(user)
    }

    override fun authenticateWithGoogle(idToken: String): TokenPair {
        val googleIdToken = try {
            verifier.verify(idToken) ?: throw AuthenticationException("Invalid Google ID token")
        } catch (ex: Exception) {
            throw AuthenticationException("Error validating Google ID token", ex)
        }

        val payload = googleIdToken.payload
        val email = payload.email
        val name = payload["name"] as? String ?: email

        // Find user by email or create a new one
        val user = userService.findByUsername(email)
            ?: createUserFromGoogle(email, name, payload.subject)

        return tokenProvider.generateTokenPair(user)
    }

    private fun createUserFromGoogle(email: String, name: String, googleId: String): User {
        // Create a new user with information from Google
        val user = User(
            username = email,
            firstName = name.split(" ").firstOrNull() ?: "",
            lastName = name.split(" ").drop(1).joinToString(" "),
            email = email
        )

        return userService.createUser(user)
    }

    override fun refreshToken(refreshToken: String): TokenPair {
        val userId = try {
            tokenProvider.validateRefreshToken(refreshToken)
        } catch (ex: Exception) {
            throw AuthenticationException("Invalid refresh token", ex)
        }

        val user = userService.findById(UUID.fromString(userId))
            ?: throw AuthenticationException("User not found")

        return tokenProvider.generateTokenPair(user)
    }

    override fun validateToken(accessToken: String): User {
        val userId = try {
            tokenProvider.validateAccessToken(accessToken)
        } catch (ex: Exception) {
            throw AuthenticationException("Invalid access token", ex)
        }

        return userService.findById(UUID.fromString(userId))
            ?: throw AuthenticationException("User not found")
    }

    override fun invalidateTokens(userId: String) {
        tokenProvider.invalidateTokens(userId)
    }
}
