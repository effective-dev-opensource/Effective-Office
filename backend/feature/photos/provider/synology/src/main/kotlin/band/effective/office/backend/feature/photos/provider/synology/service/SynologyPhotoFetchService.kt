package band.effective.office.backend.feature.photos.provider.synology.service

import band.effective.office.backend.feature.photos.core.exception.PhotosRetrievalFailedException
import band.effective.office.backend.feature.photos.core.domain.model.Photo
import band.effective.office.backend.feature.photos.provider.synology.api.SynologyApi
import band.effective.office.backend.feature.photos.provider.synology.config.SynologyCredentials
import band.effective.office.backend.feature.photos.provider.synology.constants.SynologyApiConstants
import band.effective.office.backend.feature.photos.provider.synology.dto.AlbumDTO
import band.effective.office.backend.feature.photos.provider.synology.dto.SynologyApiResponseDTO
import band.effective.office.backend.feature.photos.provider.synology.mapper.SynologyPhotoMapper
import org.springframework.stereotype.Service

@Service
class SynologyPhotoFetchService(
    private val synologyApi: SynologyApi,
    private val credentials: SynologyCredentials
) {
    fun retrieveAllPhotosFromAlbums(sid: String, albums: List<AlbumDTO>): List<Photo> {
        val allPhotos = mutableListOf<Photo>()
        for (album in albums) {
            val photos = retrievePhotosFromAlbum(sid, album, null)
            allPhotos.addAll(photos)
        }
        return allPhotos
    }

    fun retrievePhotosFromAlbum(sid: String, album: AlbumDTO, limit: Int?): List<Photo> {
        val itemsLimit = (limit ?: album.itemCount).coerceAtMost(album.itemCount)

        val parsed: SynologyApiResponseDTO = runCatching {
            synologyApi.photos(
                api = SynologyApiConstants.PHOTOS_API,
                version = SynologyApiConstants.PHOTOS_VERSION,
                method = SynologyApiConstants.LIST_METHOD,
                offset = SynologyApiConstants.DEFAULT_OFFSET,
                limit = itemsLimit,
                albumId = album.id,
                additional = SynologyApiConstants.PHOTO_ADDITIONAL_FIELDS,
                sid = sid
            )
        }.getOrElse { throw PhotosRetrievalFailedException("Synology photos request failed for album ${album.id}: ${it.message}") }

        if (!parsed.success || parsed.photoData == null) {
            throw PhotosRetrievalFailedException("Failed to retrieve photos from album ${album.id}: success=${parsed.success}")
        }

        return parsed.photoData.photosInfo
            .filter { it.type == SynologyApiConstants.PHOTO_TYPE }
            .map { photoInfo ->
                val dto = SynologyPhotoMapper.toPhotoModel(photoInfo, sid, credentials.url)
                Photo(
                    id = photoInfo.id.toString(),
                    thumbnailUrl = dto.photoThumb
                )
            }
    }
}