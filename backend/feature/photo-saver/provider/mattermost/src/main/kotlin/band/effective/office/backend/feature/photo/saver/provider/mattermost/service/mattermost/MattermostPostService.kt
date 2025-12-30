package band.effective.office.backend.feature.photo.saver.provider.mattermost.service.mattermost

import band.effective.office.backend.feature.photo.saver.core.domain.model.Post
import band.effective.office.backend.feature.photo.saver.core.exception.DataRetrievalException
import band.effective.office.backend.feature.photo.saver.provider.mattermost.api.MattermostApi
import band.effective.office.backend.feature.photo.saver.provider.mattermost.config.MattermostCredentials
import band.effective.office.backend.feature.photo.saver.provider.mattermost.dto.PostDTO
import band.effective.office.backend.feature.photo.saver.provider.mattermost.mapper.toPost
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

/**
 * Service for fetching posts from Mattermost channels.
 */
@Service
class MattermostPostService(
    @Qualifier("photoSaverMattermostApi") private val mattermostApi: MattermostApi,
    @Qualifier("photoSaverMattermostCredentials") private val credentials: MattermostCredentials
) {
    private val logger = LoggerFactory.getLogger(MattermostPostService::class.java)

    /**
     * Retrieves all posts from all channels since the specified timestamp.
     * Delegates to getPostDTOsFromChannels and maps to domain models.
     */
    fun getPostsFromChannels(sinceTimestamp: Long): List<Post> {
        return getPostDTOsFromChannels(sinceTimestamp).map { it.toPost() }
    }

    /**
     * Retrieves raw post DTOs from all channels since the specified timestamp.
     */
    fun getPostDTOsFromChannels(sinceTimestamp: Long): List<PostDTO> {
        return try {
            val channels = mattermostApi.getChannels()
            logger.info("Retrieved ${channels.size} channels")

            val allPosts = mutableListOf<PostDTO>()

            channels.forEach { channel ->
                try {
                    val response = mattermostApi.getPostsFromChannel(
                        channelId = channel.id,
                        since = sinceTimestamp
                    )
                    allPosts.addAll(response.posts.values)
                } catch (e: Exception) {
                    logger.warn("Failed to get posts from channel ${channel.id}: ${e.message}")
                }
            }

            logger.info("Retrieved ${allPosts.size} post DTOs from all channels")
            allPosts
        } catch (e: Exception) {
            logger.error("Failed to get post DTOs from channels: ${e.message}", e)
            throw DataRetrievalException("Failed to get post DTOs from channels")
        }
    }
}
