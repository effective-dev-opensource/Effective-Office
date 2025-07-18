package band.effective.office.backend.feature.authorization.config

/**
 * Centralized configuration for public endpoints that don't require authentication.
 * This class provides a single source of truth for paths that should be accessible without authentication.
 */
object PublicEndpoints {
    /**
     * List of ANT patterns for public endpoints.
     */
    val PATTERNS = listOf(
        "/auth/**",
        "/swagger-ui.html/**",
        "/swagger-ui/**",
        "/api-docs/**",
        "/v3/api-docs/**",
        "/api/swagger-ui.html/**",
        "/api/swagger-ui/**",
        "/api/api-docs/**",
        "/api/v3/api-docs/**",
        "/api/actuator/**",
        "/api/notifications/**",
        "/notifications",
    )

    /**
     * Checks if the given URI matches any of the public endpoint patterns.
     *
     * @param uri The URI to check
     * @return True if the URI matches any public endpoint pattern, false otherwise
     */
    fun matches(uri: String): Boolean {
        return PATTERNS.any { pattern ->
            // Convert ANT pattern to regex pattern
            val regexPattern = pattern
                .replace("/**", "(/.*)?") // /** matches zero or more path segments
                .replace("/*", "(/[^/]*)?") // /* matches zero or one path segment
                .replace("*", "[^/]*") // * matches zero or more characters within a path segment
            
            uri.matches(Regex("^$regexPattern$"))
        }
    }
}