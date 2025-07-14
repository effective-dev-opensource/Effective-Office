package band.effective.office.backend.feature.authorization.core

import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Manages a chain of authorizers using the Chain of Responsibility pattern.
 * This class is responsible for building and executing the authorization chain.
 */
@Component
class AuthorizationChain {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private var head: Authorizer? = null
    private var tail: Authorizer? = null
    
    /**
     * Adds an authorizer to the end of the chain.
     *
     * @param authorizer The authorizer to add
     * @return This chain for method chaining
     */
    fun addAuthorizer(authorizer: Authorizer): AuthorizationChain {
        if (head == null) {
            head = authorizer
            tail = authorizer
        } else {
            tail?.setNext(authorizer)
            tail = authorizer
        }
        logger.debug("Added authorizer: {}", authorizer::class.simpleName)
        return this
    }
    
    /**
     * Attempts to authorize the request using the chain of authorizers.
     *
     * @param request The HTTP request to authorize
     * @return The authorization result
     */
    fun authorize(request: HttpServletRequest): AuthorizationResult {
        if (head == null) {
            logger.warn("Authorization chain is empty")
            return AuthorizationResult(false)
        }
        
        return head!!.authorize(request)
    }
    
    /**
     * Checks if the chain is empty.
     *
     * @return True if the chain has no authorizers, false otherwise
     */
    fun isEmpty(): Boolean {
        return head == null
    }
}