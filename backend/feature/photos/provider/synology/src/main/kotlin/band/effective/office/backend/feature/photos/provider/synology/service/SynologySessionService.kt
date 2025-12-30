package band.effective.office.backend.feature.photos.provider.synology.service

import band.effective.office.backend.feature.photos.provider.synology.constants.SynologyApiConstants
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class SynologySessionService(
    private val authService: SynologyAuthService
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
    private val sessionCache = ConcurrentHashMap<String, String>()

    /**
     * Returns a valid Session ID, automatically refreshing it when necessary.
     * Validates cached session via SYNO.API.Auth method "info" and creates a new one if expired.
     */
    fun getValidSessionId(): String {
        val cached = sessionCache[SynologyApiConstants.SESSION_CACHE_KEY]
        
        // If cached session exists, validate it
        if (cached != null) {
            logger.debug("Found cached session ID: ${cached.take(8)}..., validating...")
            
            if (authService.isSessionValid(cached)) {
                logger.debug("Cached session is valid, reusing")
                return cached
            } else {
                logger.info("Cached session is invalid or expired, creating new session")
                sessionCache.remove(SynologyApiConstants.SESSION_CACHE_KEY)
            }
        }

        // Create new session
        logger.info("Creating new Synology session")
        val auth = authService.authenticate()
        sessionCache[SynologyApiConstants.SESSION_CACHE_KEY] = auth.sid
        logger.info("New session created successfully: ${auth.sid.take(8)}...")
        
        return auth.sid
    }

    /**
     * Forcefully invalidates and removes the cached session.
     * Used when API errors occur to force reauthentication.
     */
    fun invalidateSession() {
        logger.warn("Invalidating cached Synology session")
        sessionCache.remove(SynologyApiConstants.SESSION_CACHE_KEY)
    }
}