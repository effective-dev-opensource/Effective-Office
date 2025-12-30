package band.effective.office.backend.feature.photos.provider.synology.model

/**
 * Domain model representing Synology authentication data.
 * Contains the session ID required for authenticated API requests.
 */
data class SynologyAuthModel(
    val sid: String
)