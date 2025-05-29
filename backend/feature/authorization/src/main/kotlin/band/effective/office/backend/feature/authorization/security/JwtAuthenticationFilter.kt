package band.effective.office.backend.feature.authorization.security

import band.effective.office.backend.feature.authorization.exception.AuthenticationException
import band.effective.office.backend.feature.authorization.service.AuthorizationService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filter that extracts and validates the JWT token from the request headers.
 */
class JwtAuthenticationFilter(
    private val authorizationService: AuthorizationService
) : OncePerRequestFilter() {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // Skip filter for login and refresh endpoints
        val path = request.requestURI
        if (path.endsWith("/auth/login") || path.endsWith("/auth/refresh")) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            // Extract token from header
            val authHeader = request.getHeader(AUTHORIZATION_HEADER)
            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                filterChain.doFilter(request, response)
                return
            }

            val token = authHeader.substring(BEARER_PREFIX.length)

            // Validate token and get user
            val user = authorizationService.validateToken(token)

            // Create authentication token
            val authorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
            val authentication = UsernamePasswordAuthenticationToken(
                user.username,
                null,
                authorities
            )
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

            // Set authentication in context
            SecurityContextHolder.getContext().authentication = authentication

            filterChain.doFilter(request, response)
        } catch (ex: AuthenticationException) {
            // If token validation fails, continue the filter chain without setting authentication
            filterChain.doFilter(request, response)
        } catch (ex: Exception) {
            // For any other exception, continue the filter chain without setting authentication
            filterChain.doFilter(request, response)
        }
    }
}
