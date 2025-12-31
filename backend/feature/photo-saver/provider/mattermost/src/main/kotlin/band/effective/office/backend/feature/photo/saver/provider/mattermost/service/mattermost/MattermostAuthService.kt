package band.effective.office.backend.feature.photo.saver.provider.mattermost.service.mattermost

import band.effective.office.backend.feature.photo.saver.core.exception.ProviderConnectionException
import band.effective.office.backend.feature.photo.saver.provider.mattermost.api.MattermostApi
import band.effective.office.backend.feature.photo.saver.provider.mattermost.config.MattermostCredentials
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

/**
 * Service for Mattermost authentication and user operations.
 */
@Service
class MattermostAuthService(
    @Qualifier("photoSaverMattermostApi") private val mattermostApi: MattermostApi,
    @Qualifier("photoSaverMattermostCredentials") private val credentials: MattermostCredentials
) {
    private val logger = LoggerFactory.getLogger(MattermostAuthService::class.java)
    
    @Volatile
    private var cachedUserId: String? = null

    /**
     * Returns the current user ID, fetching it from API if not cached.
     * User ID is permanent and doesn't change during application lifetime.
     */
    fun getCurrentUserId(): String {
        return cachedUserId ?: fetchAndCacheUserId()
    }
    
    private fun fetchAndCacheUserId(): String {
        return try {
            val userInfo = mattermostApi.getUserInfo()
            val userId = userInfo.userId
            cachedUserId = userId
            logger.info("Authenticated Mattermost user: ${userInfo.username} (ID: $userId)")
            userId
        } catch (e: Exception) {
            logger.error("Failed to get user info: ${e.message}", e)
            throw ProviderConnectionException("Failed to authenticate with Mattermost: ${e.message}")
        }
    }
}
