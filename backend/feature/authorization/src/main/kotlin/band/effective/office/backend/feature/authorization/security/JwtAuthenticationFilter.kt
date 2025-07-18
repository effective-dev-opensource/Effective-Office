package band.effective.office.backend.feature.authorization.security

import band.effective.office.backend.core.data.ErrorDto
import band.effective.office.backend.feature.authorization.config.PublicEndpoints
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
     * Determines whether the filter should not be applied to this request.
     * This method is called by the OncePerRequestFilter before doFilterInternal.
     * 
     * @param request The HTTP request
     * @return True if the filter should not be applied, false otherwise
     */
    override fun shouldNotFilter(request: HttpServletRequest): Boolean = PublicEndpoints.matches(request.requestURI)

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
