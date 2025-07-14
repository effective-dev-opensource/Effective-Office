package band.effective.office.backend.feature.authorization.apikey

import band.effective.office.backend.feature.authorization.apikey.repository.ApiKeyRepository
import band.effective.office.backend.feature.authorization.core.AuthorizationResult
import band.effective.office.backend.feature.authorization.core.BaseAuthorizer
import band.effective.office.backend.feature.authorization.exception.InvalidApiKeyException
import band.effective.office.backend.feature.authorization.exception.MissingTokenException
import jakarta.servlet.http.HttpServletRequest
import java.security.MessageDigest
import org.springframework.stereotype.Component

/**
 * Authorizer that verifies API keys stored in the database.
 * This authorizer extracts a token from the Authorization header,
 * hashes it, and checks if it exists in the database.
 */
@Component
class ApiKeyAuthorizer(
    private val apiKeyRepository: ApiKeyRepository,
) : BaseAuthorizer() {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
        private const val HASH_ALGORITHM = "SHA-256"
    }

    /**
     * Attempts to authorize the request using an API key.
     *
     * @param request The HTTP request to authorize
     * @return The authorization result
     * @throws MissingTokenException if the Authorization header is missing or not a Bearer token
     * @throws InvalidApiKeyException if the API key is invalid or not found
     */
    override fun doAuthorize(request: HttpServletRequest): AuthorizationResult {
        // Extract token from header
        val authHeader = request.getHeader(AUTHORIZATION_HEADER)
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            logger.debug("No Authorization header or not a Bearer token")
            throw MissingTokenException("Authorization header is missing or not a Bearer token")
        }

        val token = authHeader.substring(BEARER_PREFIX.length)
        logger.debug("Authorization header found: $token")

        try {
            // Hash the token and check if it exists in the database
            val hashedToken = encryptKey(HASH_ALGORITHM, token)
            logger.debug("Hashed token: $hashedToken")
            val apiKey = apiKeyRepository.findByKeyValue(hashedToken.lowercase())

            if (apiKey == null) {
                logger.debug("API key not found in database")
                throw InvalidApiKeyException("API key not found or invalid")
            }

            // If no user is associated with the API key, or the user wasn't found,
            // still authorize but without user information
            logger.debug("API key authorization successful (no user associated)")
            return AuthorizationResult(success = true)
        } catch (ex: InvalidApiKeyException) {
            // Re-throw authorization exceptions
            throw ex
        } catch (ex: Exception) {
            logger.error("Error validating API key: {}", ex.message)
            throw InvalidApiKeyException("Error validating API key: ${ex.message}")
        }
    }

    /**
     * Encrypts an API key using the specified hash algorithm.
     *
     * @param algorithm The hash algorithm to use (e.g., "SHA-256")
     * @param input The input string to hash
     * @return The hashed string
     */
    private fun encryptKey(algorithm: String, input: String): String {
        val bytes = MessageDigest
            .getInstance(algorithm)
            .digest(input.toByteArray())
        return bytes.joinToString("") { "%02X".format(it) }
    }
}
