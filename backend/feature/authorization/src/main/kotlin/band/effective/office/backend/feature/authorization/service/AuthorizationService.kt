package band.effective.office.backend.feature.authorization.service

import band.effective.office.backend.domain.model.User
import band.effective.office.backend.feature.authorization.model.TokenPair

/**
 * Service interface for authorization operations.
 * This interface defines the contract for any authorization implementation.
 */
interface AuthorizationService {
    /**
     * Authenticates a user with the given credentials and returns a token pair.
     *
     * @param username The username of the user.
     * @param password The password of the user.
     * @return A token pair containing access and refresh tokens.
     * @throws AuthenticationException If the credentials are invalid.
     */
    fun authenticate(username: String, password: String): TokenPair
    
    /**
     * Refreshes an access token using a refresh token.
     *
     * @param refreshToken The refresh token.
     * @return A new token pair containing fresh access and refresh tokens.
     * @throws AuthenticationException If the refresh token is invalid or expired.
     */
    fun refreshToken(refreshToken: String): TokenPair
    
    /**
     * Validates an access token and returns the associated user.
     *
     * @param accessToken The access token to validate.
     * @return The user associated with the token.
     * @throws AuthenticationException If the token is invalid or expired.
     */
    fun validateToken(accessToken: String): User
    
    /**
     * Invalidates all tokens for a user (logout).
     *
     * @param userId The ID of the user.
     */
    fun invalidateTokens(userId: String)
}