package band.effective.office.backend.feature.photo.saver.provider.mattermost.service.mattermost

import band.effective.office.backend.feature.photo.saver.core.exception.PhotoDownloadException
import band.effective.office.backend.feature.photo.saver.provider.mattermost.api.MattermostApi
import band.effective.office.backend.feature.photo.saver.provider.mattermost.config.MattermostCredentials
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service

/**
 * Service for downloading files from Mattermost.
 */
@Service
class MattermostFileDownloadService(
    @Qualifier("photoSaverMattermostApi") private val mattermostApi: MattermostApi,
    @Qualifier("photoSaverMattermostCredentials") private val credentials: MattermostCredentials
) {
    private val logger = LoggerFactory.getLogger(MattermostFileDownloadService::class.java)

    /**
     * Downloads a file from Mattermost by its ID.
     * Uses ByteArrayOutputStream for efficient memory handling.
     */
    fun downloadFile(fileId: String): ByteArray? {
        return try {
            val flux = mattermostApi.downloadFile(fileId)
            val buffers = flux.collectList().block() ?: return null
            
            val totalBytes = buffers.sumOf { it.readableByteCount() }
            val outputStream = java.io.ByteArrayOutputStream(totalBytes)
            
            buffers.forEach { buffer ->
                val bytes = ByteArray(buffer.readableByteCount())
                buffer.read(bytes)
                outputStream.write(bytes)
            }
            
            val result = outputStream.toByteArray()
            logger.debug("Downloaded file $fileId: ${result.size} bytes")
            result
        } catch (e: Exception) {
            logger.error("Failed to download file $fileId: ${e.message}", e)
            throw PhotoDownloadException("Failed to download file $fileId")
        }
    }
}
