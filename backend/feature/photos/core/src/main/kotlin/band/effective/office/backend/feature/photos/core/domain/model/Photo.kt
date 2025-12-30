package band.effective.office.backend.feature.photos.core.domain.model

/**
 * Minimal domain model representing a photo.
 */
data class Photo(
    val id: String,
    val thumbnailUrl: String
)