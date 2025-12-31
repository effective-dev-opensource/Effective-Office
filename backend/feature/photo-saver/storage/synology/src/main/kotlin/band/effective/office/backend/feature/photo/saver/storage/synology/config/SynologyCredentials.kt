package band.effective.office.backend.feature.photo.saver.storage.synology.config

/**
 * Data class for Synology credentials.
 */
data class SynologyCredentials(
    val baseUrl: String,
    val username: String,
    val password: String,
    val albumName: String
)
