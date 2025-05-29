package band.effective.office.backend.feature.authorization.model

import java.time.Instant

/**
 * Base interface for authentication tokens.
 */
interface AuthToken {
    /**
     * The token value.
     */
    val token: String
    
    /**
     * The expiration time of the token.
     */
    val expiresAt: Instant
    
    /**
     * Checks if the token is expired.
     */
    fun isExpired(): Boolean = Instant.now().isAfter(expiresAt)
}

/**
 * Represents an access token used for authentication.
 */
data class AccessToken(
    override val token: String,
    override val expiresAt: Instant
) : AuthToken

/**
 * Represents a refresh token used to obtain a new access token.
 */
data class RefreshToken(
    override val token: String,
    override val expiresAt: Instant
) : AuthToken

/**
 * Represents a pair of access and refresh tokens.
 */
data class TokenPair(
    val accessToken: AccessToken,
    val refreshToken: RefreshToken
)