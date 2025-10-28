package band.effective.office.backend.feature.photos.provider.synology.service

import band.effective.office.backend.feature.photos.core.exception.PhotosRetrievalFailedException
import band.effective.office.backend.feature.photos.provider.synology.api.SynologyApi
import band.effective.office.backend.feature.photos.provider.synology.config.SynologyCredentials
import band.effective.office.backend.feature.photos.provider.synology.constants.SynologyApiConstants
import band.effective.office.backend.feature.photos.provider.synology.dto.AlbumDTO
import band.effective.office.backend.feature.photos.provider.synology.dto.SynologyAlbumsResponseDTO
import org.springframework.stereotype.Service

@Service
class SynologyAlbumService(
    private val synologyApi: SynologyApi,
    private val credentials: SynologyCredentials,
    private val sessionService: SynologySessionService
) {
    fun fetchAlbums(sid: String): SynologyAlbumsResponseDTO = runCatching {
        synologyApi.albums(
            api = SynologyApiConstants.ALBUMS_API,
            version = SynologyApiConstants.ALBUMS_VERSION,
            method = SynologyApiConstants.LIST_METHOD,
            offset = SynologyApiConstants.DEFAULT_OFFSET,
            limit = SynologyApiConstants.DEFAULT_ALBUMS_LIMIT,
            sid = sid
        )
    }.getOrElse { throw PhotosRetrievalFailedException("Synology albums request failed: ${it.message}") }

    fun filterAlbums(response: SynologyAlbumsResponseDTO): List<AlbumDTO> {
        if (!response.success) {
            // Invalidate session for automatic refresh on next request
            sessionService.invalidateSession()
            
            throw PhotosRetrievalFailedException(
                "Failed to retrieve albums from Synology (success=false)"
            )
        }

        val base = credentials.albumName.trim()
        return if (base.isNotEmpty()) {
            response.albumsData?.albums?.filter { it.name.contains(base, ignoreCase = true) } ?: emptyList()
        } else {
            response.albumsData?.albums ?: emptyList()
        }
    }
}