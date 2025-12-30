package band.effective.office.backend.feature.photo.saver.storage.synology.service

import band.effective.office.backend.feature.photo.saver.core.exception.PhotoUploadException
import band.effective.office.backend.feature.photo.saver.storage.synology.api.SynologyApi
import band.effective.office.backend.feature.photo.saver.storage.synology.config.SynologyCredentials
import band.effective.office.backend.feature.photo.saver.storage.synology.constants.SynologyConstants
import band.effective.office.backend.feature.photo.saver.storage.synology.util.SynologyRequestBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

/**
 * Service for managing Synology photo albums for Photo Saver.
 * Uses the album name directly from configuration without any modifications.
 */
@Service("photoSaverAlbumService")
class PhotoSaverAlbumService(
    @Qualifier("photoSaverSynologyApi") private val synologyApi: SynologyApi,
    @Qualifier("photoSaverSynologyCredentials") private val credentials: SynologyCredentials,
    private val sessionService: PhotoSaverSessionService
) {
    private val logger = LoggerFactory.getLogger(PhotoSaverAlbumService::class.java)

    /**
     * Ensures that an album exists and returns its ID.
     * Creates the album if it doesn't exist.
     * Automatically retries on session expiration.
     *
     * @return The ID of the album
     */
    fun ensureAlbumExists(retryAttempt: Int = 0): Int {
        validateAlbumName()
        val albumName = getAlbumName()
        val cookie = sessionService.getValidCookie()

        val albumsResponse = synologyApi.getAlbums(
            cookie = cookie,
            api = SynologyConstants.ALBUMS_API,
            version = SynologyConstants.ALBUMS_VERSION,
            method = SynologyConstants.METHOD_LIST,
            offset = SynologyConstants.DEFAULT_OFFSET,
            limit = SynologyConstants.DEFAULT_ALBUMS_LIMIT
        )

        if (!albumsResponse.success) {
            val errorCode = albumsResponse.error?.code
            
            // Check if error is session-related and retry is allowed
            if (SynologyConstants.isSessionError(errorCode) && retryAttempt < SynologyConstants.MAX_RETRY_ATTEMPTS) {
                logger.warn("Session error (code: $errorCode), invalidating and retrying (attempt ${retryAttempt + 1})")
                sessionService.invalidateSession()
                return ensureAlbumExists(retryAttempt + 1)
            }
            
            throw PhotoUploadException("Failed to retrieve albums from Synology, error code: $errorCode")
        }

        val existingAlbum = albumsResponse.data?.albums?.find { it.name == albumName }

        val albumId = existingAlbum?.id ?: createAlbum(cookie, albumName)

        logger.info("Using album: '$albumName' (ID: $albumId)")
        return albumId
    }

    /**
     * Creates a new album with the specified name.
     */
    private fun createAlbum(cookie: String, albumName: String, retryAttempt: Int = 0): Int {
        val requestBody = SynologyRequestBuilder.buildCreateAlbumBody(albumName)

        val response = synologyApi.createAlbum(
            cookie = cookie,
            body = requestBody
        )

        logger.debug("Create album response: success=${response.success}, data=${response.data}, error=${response.error}")

        if (!response.success) {
            val errorCode = response.error?.code
            
            // Check if error is session-related and retry is allowed
            if (SynologyConstants.isSessionError(errorCode) && retryAttempt < SynologyConstants.MAX_RETRY_ATTEMPTS) {
                logger.warn("Session error (code: $errorCode) during album creation, invalidating and retrying (attempt ${retryAttempt + 1})")
                sessionService.invalidateSession()
                val newCookie = sessionService.getValidCookie()
                return createAlbum(newCookie, albumName, retryAttempt + 1)
            }
            
            throw PhotoUploadException("Failed to create album '$albumName', error code: $errorCode")
        }

        val albumId = response.data?.album?.id
            ?: throw PhotoUploadException("Failed to create album '$albumName': album ID not found in response")

        logger.info("Created new album: '$albumName' (ID: $albumId)")
        return albumId
    }
    
    /**
     * Returns the album name from configuration.
     * Uses the name exactly as configured without any modifications.
     *
     * @return The album name from configuration
     */
    private fun getAlbumName(): String {
        return credentials.albumName.trim()
    }

    /**
     * Validates that the album name is configured correctly.
     *
     * @throws IllegalStateException if album name is blank
     */
    private fun validateAlbumName() {
        require(credentials.albumName.isNotBlank()) {
            "Album name cannot be blank. Please configure PHOTO_SAVER_SYNOLOGY_ALBUM_NAME environment variable"
        }
    }
}
