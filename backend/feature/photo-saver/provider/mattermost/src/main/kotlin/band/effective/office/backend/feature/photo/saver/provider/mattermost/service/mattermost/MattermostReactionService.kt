package band.effective.office.backend.feature.photo.saver.provider.mattermost.service.mattermost

import band.effective.office.backend.feature.photo.saver.core.util.TimeProvider
import band.effective.office.backend.feature.photo.saver.provider.mattermost.api.MattermostApi
import band.effective.office.backend.feature.photo.saver.provider.mattermost.config.MattermostCredentials
import band.effective.office.backend.feature.photo.saver.provider.mattermost.dto.ReactionRequestDTO
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

/**
 * Service for managing reactions on Mattermost posts.
 */
@Service
class MattermostReactionService(
    @Qualifier("photoSaverMattermostApi") private val mattermostApi: MattermostApi,
    @Qualifier("photoSaverMattermostCredentials") private val credentials: MattermostCredentials,
    private val authService: MattermostAuthService,
    private val timeProvider: TimeProvider
) {
    private val logger = LoggerFactory.getLogger(MattermostReactionService::class.java)

    /**
     * Adds an emoji reaction to a Mattermost post.
     */
    fun addReaction(postId: String, emojiName: String) {
        try {
            val userId = authService.getCurrentUserId()
            val reaction = ReactionRequestDTO(
                createAt = timeProvider.currentTimeMillis(),
                emojiName = emojiName,
                postId = postId,
                userId = userId
            )
            mattermostApi.makeReaction(reaction)
            logger.debug("Added reaction $emojiName to post $postId")
        } catch (e: Exception) {
            logger.error("Failed to add reaction to post $postId: ${e.message}", e)
        }
    }
}
