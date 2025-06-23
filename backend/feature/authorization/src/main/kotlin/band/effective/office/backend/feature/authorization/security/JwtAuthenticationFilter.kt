package band.effective.office.backend.feature.authorization.security

import band.effective.office.backend.core.data.ErrorDto
import band.effective.office.backend.feature.authorization.exception.AuthorizationException
import band.effective.office.backend.feature.authorization.exception.AuthorizationErrorCodes
import band.effective.office.backend.feature.authorization.service.AuthorizationService
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filter that handles JWT authentication.
 * This filter uses the AuthorizationService to authorize requests.
 */
@Component
class JwtAuthenticationFilter(
    private val authorizationService: AuthorizationService,
    private val objectMapper: ObjectMapper
) : OncePerRequestFilter() {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Checks if the request is for Swagger UI or API docs.
     *
     * @param requestURI The request URI to check
     * @return True if the request is for Swagger UI or API docs, false otherwise
     */
    private fun isSwaggerUIRequest(requestURI: String): Boolean { // TODO fix this hacky code
        return requestURI.contains("/swagger-ui") ||
               requestURI.contains("/api-docs") ||
               requestURI.contains("/v3/api-docs") ||
                requestURI.contains("/notifications")
    }

    /**
     * Filters incoming requests and attempts to authenticate them.
     *
     * @param request The HTTP request
     * @param response The HTTP response
     * @param filterChain The filter chain
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Check if the request is for Swagger UI or API docs
        val requestURI = request.requestURI
        if (isSwaggerUIRequest(requestURI)) {
            logger.debug("Skipping authorization for Swagger UI request: {}", requestURI)
            filterChain.doFilter(request, response)
            return
        }

        try {
            // Attempt to authorize the request
            val result = authorizationService.authorize(request)

            // If we get here, authorization was successful
            logger.debug("Request authorized successfully")

            // Create authentication with basic authority
            val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
            val authentication = UsernamePasswordAuthenticationToken(
                "api_user", // principal
                null, // credentials (null for API key auth)
                authorities
            )

            // Set authentication in the security context
            SecurityContextHolder.getContext().authentication = authentication
            logger.debug("Authentication set in SecurityContextHolder")

            // Continue the filter chain
            filterChain.doFilter(request, response)
        } catch (ex: AuthorizationException) {
            logger.error("Authorization exception: {}", ex.message)

            // Create error response
            val errorDto = ErrorDto(
                message = ex.message,
                code = ex.errorCode
            )

            // Set response status and content type
            response.status = HttpStatus.UNAUTHORIZED.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE

            // Write error response
            objectMapper.writeValue(response.outputStream, errorDto)
        } catch (ex: Exception) {
            logger.error("Unexpected error during authorization: {}", ex.message)

            // Create error response for unexpected exceptions
            val errorDto = ErrorDto(
                message = "Internal server error during authorization",
                code = AuthorizationErrorCodes.AUTHORIZATION_SERVER_ERROR
            )

            // Set response status and content type
            response.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
            response.contentType = MediaType.APPLICATION_JSON_VALUE

            // Write error response
            objectMapper.writeValue(response.outputStream, errorDto)
        }
    }
}
