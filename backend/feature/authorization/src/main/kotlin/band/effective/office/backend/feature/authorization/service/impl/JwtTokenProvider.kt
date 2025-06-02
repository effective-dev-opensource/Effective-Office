package band.effective.office.backend.feature.authorization.service.impl

import band.effective.office.backend.core.domain.model.User
import band.effective.office.backend.feature.authorization.exception.InvalidTokenException
import band.effective.office.backend.feature.authorization.exception.TokenExpiredException
import band.effective.office.backend.feature.authorization.model.AccessToken
import band.effective.office.backend.feature.authorization.model.RefreshToken
import band.effective.office.backend.feature.authorization.model.TokenPair
import band.effective.office.backend.feature.authorization.service.TokenProvider
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SignatureException
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

/**
 * JWT implementation of the TokenProvider interface.
 */
@Service
class JwtTokenProvider(
    @Value("\${jwt.secret:defaultSecretKeyForDevelopmentOnly}") private val secret: String,
    @Value("\${jwt.access-token-expiration-ms:900000}") private val accessTokenExpirationMs: Long, // 15 minutes
    @Value("\${jwt.refresh-token-expiration-ms:2592000000}") private val refreshTokenExpirationMs: Long // 30 days
) : TokenProvider {

    private val secretKey: SecretKey by lazy {
        // Use Keys.secretKeyFor to generate a key that's guaranteed to be secure enough for HS512
        Keys.secretKeyFor(SignatureAlgorithm.HS512)
    }

    // In-memory storage for invalidated tokens (in a real application, this would be a database or Redis)
    private val invalidatedTokens: MutableSet<String> = mutableSetOf()

    override fun generateTokenPair(user: User): TokenPair {
        val now = Instant.now()

        val accessTokenExpiration = now.plusMillis(accessTokenExpirationMs)
        val accessToken = generateToken(user, accessTokenExpiration, "access")

        val refreshTokenExpiration = now.plusMillis(refreshTokenExpirationMs)
        val refreshToken = generateToken(user, refreshTokenExpiration, "refresh")

        return TokenPair(
            AccessToken(accessToken, accessTokenExpiration),
            RefreshToken(refreshToken, refreshTokenExpiration)
        )
    }

    override fun validateAccessToken(token: String): String {
        return validateToken(token, "access")
    }

    override fun validateRefreshToken(token: String): String {
        return validateToken(token, "refresh")
    }

    override fun invalidateTokens(userId: String) {
        // In a real application, this would invalidate tokens in a database or Redis
        invalidatedTokens.add(userId)
    }

    private fun generateToken(user: User, expiration: Instant, type: String): String {
        return Jwts.builder()
            .setSubject(user.id.toString())
            .claim("type", type)
            .claim("username", user.username)
            .setIssuedAt(Date.from(Instant.now()))
            .setExpiration(Date.from(expiration))
            .signWith(secretKey, SignatureAlgorithm.HS512)
            .compact()
    }

    private fun validateToken(token: String, expectedType: String): String {
        try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .body

            // Check if token is of the expected type
            val tokenType = claims.get("type", String::class.java)
            if (tokenType != expectedType) {
                throw InvalidTokenException("Invalid token type: expected $expectedType but got $tokenType")
            }

            // Check if token has been invalidated
            val userId = claims.subject
            if (invalidatedTokens.contains(userId)) {
                throw InvalidTokenException("Token has been invalidated")
            }

            return userId
        } catch (ex: ExpiredJwtException) {
            throw TokenExpiredException("Token has expired", ex)
        } catch (ex: UnsupportedJwtException) {
            throw InvalidTokenException("Unsupported JWT token", ex)
        } catch (ex: MalformedJwtException) {
            throw InvalidTokenException("Malformed JWT token", ex)
        } catch (ex: SignatureException) {
            throw InvalidTokenException("Invalid JWT signature", ex)
        } catch (ex: IllegalArgumentException) {
            throw InvalidTokenException("JWT claims string is empty", ex)
        }
    }
}
