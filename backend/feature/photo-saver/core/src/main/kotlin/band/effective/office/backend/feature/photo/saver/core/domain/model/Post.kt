package band.effective.office.backend.feature.photo.saver.core.domain.model

/**
 * Simplified post model containing only the fields needed for business logic.
 * Represents a post from a source provider that may contain photos to sync.
 */
data class Post(
    val id: String,
    val channelId: String?,
    val fileIds: List<String>?,
    val reactions: List<Reaction>?
)
