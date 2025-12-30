package band.effective.office.backend.feature.photo.saver.storage.synology.service

import band.effective.office.backend.feature.photo.saver.core.exception.PhotoUploadException
import band.effective.office.backend.feature.photo.saver.storage.synology.api.SynologyApi
import band.effective.office.backend.feature.photo.saver.storage.synology.constants.SynologyConstants
import band.effective.office.backend.feature.photo.saver.storage.synology.util.SynologyRequestBuilder
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

/**
 * Service for uploading photos to Synology NAS.
 */
@Service
class SynologyPhotoUploadService(
    @Qualifier("photoSaverSynologyApi") private val synologyApi: SynologyApi,
    private val albumService: PhotoSaverAlbumService,
    @Qualifier("photoSaverSessionService") private val sessionService: PhotoSaverSessionService
) {
    private val logger = LoggerFactory.getLogger(SynologyPhotoUploadService::class.java)

    /**
     * Uploads a photo to Synology and adds it to the configured album.
     * Automatically retries on session errors (105, 106).
     */
    fun uploadPhoto(fileBytes: ByteArray, fileName: String, mimeType: String, retryAttempt: Int = 0): Boolean {
        return try {
            val albumId = albumService.ensureAlbumExists()
            val cookie = sessionService.getValidCookie()
            
            // Upload photo to Synology
            val resource = object : org.springframework.core.io.ByteArrayResource(fileBytes) {
                override fun getFilename(): String = fileName
            }
            
            val uploadResponse = synologyApi.uploadPhoto(
                cookie = cookie,
                file = resource,
                name = "\"$fileName\"",
                duplicate = "\"ignore\""
            )
            
            logger.debug("Upload photo response: success=${uploadResponse.success}, data=${uploadResponse.data}, error=${uploadResponse.error}")
            
            if (!uploadResponse.success) {
                val errorCode = uploadResponse.error?.code
                
                // Check if error is session-related and retry is allowed
                if (SynologyConstants.isSessionError(errorCode) && retryAttempt < SynologyConstants.MAX_RETRY_ATTEMPTS) {
                    logger.warn("Session error (code: $errorCode) during photo upload, invalidating and retrying (attempt ${retryAttempt + 1})")
                    sessionService.invalidateSession()
                    return uploadPhoto(fileBytes, fileName, mimeType, retryAttempt + 1)
                }
                
                throw PhotoUploadException("Failed to upload photo: error code $errorCode")
            }
            
            val itemId = uploadResponse.data?.id
                ?: throw PhotoUploadException("No item ID in upload response. Response: success=${uploadResponse.success}, data=${uploadResponse.data}")
            
            logger.info("Uploaded photo $fileName, item ID: $itemId")
            
            // Add photo to album
            val addBody = SynologyRequestBuilder.buildAddToAlbumBody(itemId, albumId)
            val addResponse = synologyApi.addPhotoToAlbum(
                cookie = cookie,
                body = addBody
            )
            
            if (!addResponse.success) {
                val errorCode = addResponse.error?.code
                
                // Check if error is session-related and retry is allowed
                if (SynologyConstants.isSessionError(errorCode) && retryAttempt < SynologyConstants.MAX_RETRY_ATTEMPTS) {
                    logger.warn("Session error (code: $errorCode) when adding to album, invalidating and retrying (attempt ${retryAttempt + 1})")
                    sessionService.invalidateSession()
                    return uploadPhoto(fileBytes, fileName, mimeType, retryAttempt + 1)
                }
                
                logger.warn("Failed to add photo to album: error code $errorCode")
            }
            
            logger.info("Successfully uploaded and added photo $fileName to album $albumId")
            true
        } catch (e: Exception) {
            logger.error("Failed to upload photo $fileName: ${e.message}", e)
            throw PhotoUploadException("Failed to upload photo $fileName: ${e.message}")
        }
    }
}
