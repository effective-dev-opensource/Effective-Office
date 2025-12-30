package band.effective.office.backend.feature.photo.saver.core.domain.model

/**
 * Reaction on a post.
 * Used to identify which posts have been marked for photo synchronization.
 */
data class Reaction(
    val emojiName: String,
    val postId: String,
    val userId: String
)
