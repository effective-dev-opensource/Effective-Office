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

    fun getValidSessionId(): String {
        val cached = sessionCache[SynologyApiConstants.SESSION_CACHE_KEY]
        if (cached != null) return cached

        val auth = authService.authenticate()
        sessionCache[SynologyApiConstants.SESSION_CACHE_KEY] = auth.sid
        logger.debug("Created new session ID")
        return auth.sid
    }
}