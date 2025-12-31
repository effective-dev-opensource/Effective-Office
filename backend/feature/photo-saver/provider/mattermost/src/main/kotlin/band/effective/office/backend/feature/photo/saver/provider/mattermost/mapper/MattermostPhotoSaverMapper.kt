package band.effective.office.backend.feature.photo.saver.provider.mattermost.mapper

import band.effective.office.backend.feature.photo.saver.core.domain.model.FileInfo
import band.effective.office.backend.feature.photo.saver.core.domain.model.Post
import band.effective.office.backend.feature.photo.saver.core.domain.model.Reaction
import band.effective.office.backend.feature.photo.saver.provider.mattermost.dto.FileDTO
import band.effective.office.backend.feature.photo.saver.provider.mattermost.dto.PostDTO
import band.effective.office.backend.feature.photo.saver.provider.mattermost.dto.ReactionDTO

/**
 * Extension functions for converting Mattermost API DTOs to domain models.
 */

/**
 * Converts Mattermost post DTO to domain Post model.
 */
fun PostDTO.toPost(): Post = Post(
    id = id ?: "",
    channelId = channelId,
    fileIds = fileIds,
    reactions = metadata?.reactions?.map { it.toReaction() }
)

/**
 * Converts Mattermost reaction DTO to domain Reaction model.
 */
fun ReactionDTO.toReaction(): Reaction = Reaction(
    emojiName = emojiName,
    postId = postId,
    userId = userId
)

/**
 * Converts Mattermost file DTO to FileInfo domain model.
 */
fun FileDTO.toFileInfo(): FileInfo = FileInfo(
    id = id,
    fileName = name,
    fileType = mimeType,
    postId = postId
)
