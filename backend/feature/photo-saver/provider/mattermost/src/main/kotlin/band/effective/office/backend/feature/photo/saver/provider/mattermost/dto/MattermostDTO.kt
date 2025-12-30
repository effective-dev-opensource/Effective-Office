package band.effective.office.backend.feature.photo.saver.provider.mattermost.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Mattermost API DTOs with Jackson annotations.
 */

data class MattermostChannelDTO(
    @JsonProperty("id") val id: String,
    @JsonProperty("display_name") val displayName: String,
    @JsonProperty("name") val name: String
)

data class MattermostPostsResponseDTO(
    @JsonProperty("has_next") val hasNext: Boolean,
    @JsonProperty("next_post_id") val nextPostId: String?,
    @JsonProperty("order") val order: List<String>,
    @JsonProperty("posts") val posts: Map<String, PostDTO>,
    @JsonProperty("prev_post_id") val prevPostId: String?
)

data class PostDTO(
    @JsonProperty("id") val id: String?,
    @JsonProperty("create_at") val createAt: Long?,
    @JsonProperty("channel_id") val channelId: String?,
    @JsonProperty("file_ids") val fileIds: List<String>?,
    @JsonProperty("metadata") val metadata: MetadataDTO?
)

data class MetadataDTO(
    @JsonProperty("reactions") val reactions: List<ReactionDTO>?,
    @JsonProperty("files") val files: List<FileDTO>?
)

data class ReactionDTO(
    @JsonProperty("emoji_name") val emojiName: String,
    @JsonProperty("post_id") val postId: String,
    @JsonProperty("user_id") val userId: String
)

data class FileDTO(
    @JsonProperty("id") val id: String,
    @JsonProperty("mime_type") val mimeType: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("post_id") val postId: String
)

data class ReactionRequestDTO(
    @JsonProperty("create_at") val createAt: Long,
    @JsonProperty("emoji_name") val emojiName: String,
    @JsonProperty("post_id") val postId: String,
    @JsonProperty("user_id") val userId: String
)

data class MattermostUserInfoDTO(
    @JsonProperty("id") val userId: String,
    @JsonProperty("username") val username: String,
    @JsonProperty("email") val email: String?
)
