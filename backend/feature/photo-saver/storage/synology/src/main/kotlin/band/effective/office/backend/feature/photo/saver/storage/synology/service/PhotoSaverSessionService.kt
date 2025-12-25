package band.effective.office.backend.feature.photo.saver.storage.synology.service

import band.effective.office.backend.feature.photo.saver.core.exception.PhotoUploadException
import band.effective.office.backend.feature.photo.saver.storage.synology.api.SynologyApi
import band.effective.office.backend.feature.photo.saver.storage.synology.config.SynologyCredentials
import band.effective.office.backend.feature.photo.saver.storage.synology.constants.SynologyConstants
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

/**
 * Service for managing Synology authentication sessions for Photo Saver.
 * Validates cached session via SYNO.API.Auth method "info" and creates a new one if expired.
 */
@Service("photoSaverSessionService")
class PhotoSaverSessionService(
    @Qualifier("photoSaverSynologyApi") private val synologyApi: SynologyApi,
    @Qualifier("photoSaverSynologyCredentials") private val credentials: SynologyCredentials
) {
    private val logger = LoggerFactory.getLogger(PhotoSaverSessionService::class.java)
    
    @Volatile
    private var cachedSession: String? = null

    /**
     * Returns a valid Session ID (cookie), automatically refreshing it when necessary.
     * Validates cached session and creates a new one if expired.
     */
    fun getValidCookie(): String {
        val cached = cachedSession
        
        // If cached session exists, validate it
        if (cached != null) {
            logger.debug("Found cached session, validating...")
            
            if (isSessionValid(cached)) {
                logger.debug("Cached session is valid, reusing")
                return cached
            } else {
                logger.info("Cached session is invalid or expired, creating new session")
                cachedSession = null
            }
        }
        
        // Create new session
        logger.info("Creating new Synology session")
        return login()
    }
    
    /**
     * Forcefully invalidates and removes the cached session.
     * Used when API errors occur to force reauthentication.
     */
    fun invalidateSession() {
        logger.warn("Invalidating cached Synology session")
        cachedSession = null
    }
    
    /**
     * Validates existing session via SYNO.API.Auth method "info"
     * @param cookie Session cookie to validate
     * @return true if session is valid, false if expired or invalid
     */
    private fun isSessionValid(cookie: String): Boolean {
        return runCatching {
            // Extract SID from cookie
            val sid = extractSidFromCookie(cookie) ?: return false
            
            val response = synologyApi.checkSession(
                api = SynologyConstants.AUTH_API,
                version = SynologyConstants.AUTH_VERSION,
                method = SynologyConstants.METHOD_INFO,
                session = SynologyConstants.AUTH_SESSION,
                sid = sid
            )
            
            val isValid = response.success
            logger.debug("Session validation result: $isValid")
            isValid
        }.getOrElse { 
            logger.warn("Session validation failed with exception: ${it.message}")
            false 
        }
    }
    
    /**
     * Extracts SID value from cookie string
     */
    private fun extractSidFromCookie(cookie: String): String? {
        return cookie.split(";")
            .map { it.trim() }
            .firstOrNull { it.startsWith("id=") }
            ?.substringAfter("id=")
    }

    /**
     * Performs login to Synology and caches the session.
     */
    private fun login(): String {
        return try {
            logger.debug("Logging in to Synology")
            val response = synologyApi.auth(
                api = SynologyConstants.AUTH_API,
                version = SynologyConstants.AUTH_VERSION,
                method = SynologyConstants.METHOD_LOGIN,
                account = credentials.username,
                passwd = credentials.password,
                session = SynologyConstants.AUTH_SESSION,
                format = SynologyConstants.AUTH_FORMAT
            )

            val body = response.body
            if (body == null || !body.success) {
                val errorCode = body?.error?.code ?: "unknown"
                throw PhotoUploadException("Synology auth failed: success=${body?.success}, error code=$errorCode")
            }
            
            // Extract cookie from Set-Cookie headers
            val setCookieHeaders = response.headers["Set-Cookie"] ?: emptyList()
            val cookie = setCookieHeaders.joinToString("; ")
            
            if (cookie.isEmpty()) {
                throw PhotoUploadException("No cookie in auth response")
            }
            
            // Cache the session
            cachedSession = cookie
            logger.info("Successfully logged in to Synology, session cached")
            cookie
        } catch (e: Exception) {
            logger.error("Failed to login to Synology: ${e.message}", e)
            throw PhotoUploadException("Synology authentication failed: ${e.message}")
        }
    }
}
