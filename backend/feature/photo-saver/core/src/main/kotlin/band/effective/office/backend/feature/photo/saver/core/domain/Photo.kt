package band.effective.office.backend.feature.photo.saver.core.domain

import java.time.Instant

/**
 * Domain model representing a photo from any provider.
 * 
 * @property fileBytes Binary content of the photo
 * @property fileName Original filename (e.g., "photo.jpg")
 * @property mimeType MIME type (e.g., "image/jpeg")
 * @property metadata Additional metadata from the provider
 */
data class Photo(
    val fileBytes: ByteArray,
    val fileName: String,
    val mimeType: String,
    val metadata: PhotoMetadata
)

/**
 * Metadata associated with a photo.
 * 
 * @property providerName Name of the provider (e.g., "Mattermost", "Telegram")
 * @property originalId Original ID from the provider source
 * @property createdAt Timestamp when photo was created/posted
 * @property authorId ID of the person who posted the photo
 * @property authorName Display name of the author
 */
data class PhotoMetadata(
    val providerName: String,
    val originalId: String,
    val createdAt: Instant,
    val authorId: String? = null,
    val authorName: String? = null
)
