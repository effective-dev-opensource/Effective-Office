package band.effective.office.backend.feature.authorization.service

import band.effective.office.backend.domain.model.User
import band.effective.office.backend.feature.authorization.model.AccessToken
import band.effective.office.backend.feature.authorization.model.RefreshToken
import band.effective.office.backend.feature.authorization.model.TokenPair

/**
 * Interface for token providers.
 * This interface defines the contract for any token provider implementation.
 */
interface TokenProvider {
    /**
     * Generates a token pair for a user.
     *
     * @param user The user to generate tokens for.
     * @return A token pair containing access and refresh tokens.
     */
    fun generateTokenPair(user: User): TokenPair
    
    /**
     * Validates an access token and returns the user ID.
     *
     * @param token The access token to validate.
     * @return The user ID associated with the token.
     * @throws InvalidTokenException If the token is invalid.
     * @throws TokenExpiredException If the token has expired.
     */
    fun validateAccessToken(token: String): String
    
    /**
     * Validates a refresh token and returns the user ID.
     *
     * @param token The refresh token to validate.
     * @return The user ID associated with the token.
     * @throws InvalidTokenException If the token is invalid.
     * @throws TokenExpiredException If the token has expired.
     */
    fun validateRefreshToken(token: String): String
    
    /**
     * Invalidates all tokens for a user.
     *
     * @param userId The ID of the user.
     */
    fun invalidateTokens(userId: String)
}