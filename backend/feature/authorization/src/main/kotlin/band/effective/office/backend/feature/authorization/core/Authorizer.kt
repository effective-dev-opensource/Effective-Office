package band.effective.office.backend.feature.authorization.core

import jakarta.servlet.http.HttpServletRequest

/**
 * Interface for authorization chain of responsibility element.
 * Each authorizer in the chain is responsible for a specific type of authorization.
 */
interface Authorizer {
    /**
     * Attempts to authorize the request.
     *
     * @param request The HTTP request to authorize
     * @return The authorization result containing success status and optional user information
     */
    fun authorize(request: HttpServletRequest): AuthorizationResult

    /**
     * Sets the next authorizer in the chain.
     *
     * @param next The next authorizer to process the request if this one fails
     * @return The next authorizer
     */
    fun setNext(next: Authorizer): Authorizer
}

/**
 * Result of an authorization attempt.
 *
 * @property success Whether the authorization was successful
 * @property errorMessage Optional error message if authorization failed
 * @property errorCode Optional error code if authorization failed
 */
data class AuthorizationResult(
    val success: Boolean,
    val errorMessage: String? = null,
    val errorCode: Int? = null
)
