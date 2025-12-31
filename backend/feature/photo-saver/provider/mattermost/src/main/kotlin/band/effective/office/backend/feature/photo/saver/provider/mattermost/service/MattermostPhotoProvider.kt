package band.effective.office.backend.feature.photo.saver.provider.mattermost.service

import band.effective.office.backend.feature.photo.saver.core.domain.Photo
import band.effective.office.backend.feature.photo.saver.core.domain.PhotoMetadata
import band.effective.office.backend.feature.photo.saver.core.domain.PhotoProvider
import band.effective.office.backend.feature.photo.saver.core.exception.DataRetrievalException
import band.effective.office.backend.feature.photo.saver.core.util.TimeProvider
import band.effective.office.backend.feature.photo.saver.provider.mattermost.config.MattermostCredentials
import band.effective.office.backend.feature.photo.saver.provider.mattermost.mapper.toFileInfo
import band.effective.office.backend.feature.photo.saver.provider.mattermost.service.mattermost.MattermostFileDownloadService
import band.effective.office.backend.feature.photo.saver.provider.mattermost.service.mattermost.MattermostPostService
import band.effective.office.backend.feature.photo.saver.provider.mattermost.service.mattermost.MattermostReactionService
import band.effective.office.backend.feature.photo.saver.provider.mattermost.util.PostFiltersFactory
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.time.Instant

/**
 * Mattermost implementation of PhotoProvider.
 * Fetches photos from Mattermost channels based on emoji markers.
 */
@Component("mattermostPhotoProvider")
@ConditionalOnProperty(name = ["photo.saver.provider"], havingValue = "mattermost")
class MattermostPhotoProvider(
    private val postService: MattermostPostService,
    private val fileDownloadService: MattermostFileDownloadService,
    private val reactionService: MattermostReactionService,
    @Qualifier("photoSaverMattermostCredentials") private val credentials: MattermostCredentials,
    private val timeProvider: TimeProvider
) : PhotoProvider {
    
    private val logger = LoggerFactory.getLogger(MattermostPhotoProvider::class.java)

    override suspend fun fetchNewPhotos(): List<Photo> = coroutineScope {
        try {
            val sinceTimestamp = timeProvider.yesterdayTimestamp()
            val allPosts = postService.getPostDTOsFromChannels(sinceTimestamp)
            
            logger.info("Retrieved ${allPosts.size} posts from Mattermost")
            
            val postsForSync = PostFiltersFactory.filterPostsForSync(
                posts = allPosts,
                requestSaveEmoji = credentials.emojiRequestSave,
                saveSuccessEmoji = credentials.emojiSaveSuccess
            )

            logger.info("Filtered ${postsForSync.size} posts for sync")

            val photosWithPostData = postsForSync.flatMap { post ->
                val postId = post.id ?: return@flatMap emptyList()
                val createAt = post.createAt ?: timeProvider.currentTimeMillis()
                
                post.metadata?.files?.filter { file ->
                    file.mimeType.contains("image", ignoreCase = true)
                }?.map { file ->
                    Triple(file.toFileInfo(), postId, createAt)
                } ?: emptyList()
            }

            logger.info("Found ${photosWithPostData.size} photo files to download")

            // Download all photos in parallel
            val photos = photosWithPostData.map { (fileInfo, postId, createAt) ->
                async {
                    try {
                        val fileBytes = fileDownloadService.downloadFile(fileInfo.id)
                        if (fileBytes != null) {
                            val photo = Photo(
                                fileBytes = fileBytes,
                                fileName = fileInfo.fileName,
                                mimeType = fileInfo.fileType,
                                metadata = PhotoMetadata(
                                    providerName = getProviderName(),
                                    originalId = fileInfo.id,
                                    createdAt = Instant.ofEpochMilli(createAt)
                                )
                            )
                            
                            // Mark post as successfully processed
                            reactionService.addReaction(postId, credentials.emojiSaveSuccess)
                            
                            photo
                        } else {
                            logger.warn("Failed to download file ${fileInfo.id} from post $postId")
                            null
                        }
                    } catch (e: Exception) {
                        logger.error("Error downloading file ${fileInfo.id}: ${e.message}", e)
                        null
                    }
                }
            }.awaitAll().filterNotNull()

            logger.info("Successfully downloaded ${photos.size} photos from Mattermost")
            photos
            
        } catch (e: Exception) {
            throw DataRetrievalException("Failed to fetch photos from Mattermost: ${e.message}")
        }
    }

    override fun getProviderName(): String = "Mattermost"

    override fun isHealthy(): Boolean {
        return try {
            // Check if we can fetch posts (simple health check)
            postService.getPostsFromChannels(timeProvider.currentTimeMillis() - 1000)
            true
        } catch (e: Exception) {
            logger.error("Mattermost provider health check failed: ${e.message}", e)
            false
        }
    }
}
