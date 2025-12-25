package band.effective.office.backend.feature.photo.saver.core.domain.model

/**
 * File information extracted from posts for synchronization.
 * Contains metadata about files that need to be synced to external storage.
 */
data class FileInfo(
    val id: String,
    val fileName: String,
    val fileType: String,
    val postId: String
)
