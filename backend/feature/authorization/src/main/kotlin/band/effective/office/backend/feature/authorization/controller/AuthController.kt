package band.effective.office.backend.feature.authorization.controller

import band.effective.office.backend.feature.authorization.dto.ErrorResponse
import band.effective.office.backend.feature.authorization.dto.LoginRequest
import band.effective.office.backend.feature.authorization.dto.RefreshTokenRequest
import band.effective.office.backend.feature.authorization.dto.TokenResponse
import band.effective.office.backend.feature.authorization.exception.AuthenticationException
import band.effective.office.backend.feature.authorization.exception.InvalidTokenException
import band.effective.office.backend.feature.authorization.exception.TokenExpiredException
import band.effective.office.backend.feature.authorization.service.AuthorizationService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import java.time.Duration
import kotlin.math.absoluteValue
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller for authentication endpoints.
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "API for authentication operations")
class AuthController(
    private val authorizationService: AuthorizationService
) {

    /**
     * Login endpoint.
     */
    @PostMapping("/login")
    @Operation(
        summary = "User login",
        description = "Authenticates a user and returns access and refresh tokens"
    )
    @ApiResponse(responseCode = "200", description = "Successfully authenticated")
    @ApiResponse(responseCode = "401", description = "Authentication failed")
    fun login(
        @Parameter(description = "Login credentials", required = true)
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<TokenResponse> {
        val tokenPair = authorizationService.authenticate(request.username, request.password)

        return ResponseEntity.ok(
            TokenResponse(
                accessToken = tokenPair.accessToken.token,
                refreshToken = tokenPair.refreshToken.token,
                expiresIn = Duration.between(
                    tokenPair.accessToken.expiresAt,
                    java.time.Instant.now()
                ).seconds.absoluteValue
            )
        )
    }

    /**
     * Refresh token endpoint.
     */
    @PostMapping("/refresh")
    @Operation(
        summary = "Refresh access token",
        description = "Generates a new access token using a valid refresh token"
    )
    @ApiResponse(responseCode = "200", description = "Successfully refreshed tokens")
    @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    fun refresh(
        @Parameter(description = "Refresh token", required = true)
        @Valid @RequestBody request: RefreshTokenRequest
    ): ResponseEntity<TokenResponse> {
        val tokenPair = authorizationService.refreshToken(request.refreshToken)

        return ResponseEntity.ok(
            TokenResponse(
                accessToken = tokenPair.accessToken.token,
                refreshToken = tokenPair.refreshToken.token,
                expiresIn = Duration.between(
                    tokenPair.accessToken.expiresAt,
                    java.time.Instant.now()
                ).seconds.absoluteValue
            )
        )
    }

    /**
     * Exception handler for authentication exceptions.
     */
    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse(
                status = HttpStatus.UNAUTHORIZED.value(),
                message = ex.message ?: "Authentication failed"
            )
        )
    }

    /**
     * Exception handler for invalid token exceptions.
     */
    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(ex: InvalidTokenException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse(
                status = HttpStatus.UNAUTHORIZED.value(),
                message = ex.message ?: "Invalid token"
            )
        )
    }

    /**
     * Exception handler for token expired exceptions.
     */
    @ExceptionHandler(TokenExpiredException::class)
    fun handleTokenExpiredException(ex: TokenExpiredException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
            ErrorResponse(
                status = HttpStatus.UNAUTHORIZED.value(),
                message = ex.message ?: "Token expired"
            )
        )
    }
}
