package band.effective.office.backend.feature.photos.provider.synology.service

import band.effective.office.backend.feature.photos.core.domain.PhotoProvider
import band.effective.office.backend.feature.photos.core.exception.PhotoProviderUnavailableException
import band.effective.office.backend.feature.photos.core.exception.PhotosRetrievalFailedException
import band.effective.office.backend.feature.photos.core.domain.model.Photo
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * Synology implementation of the PhotoProvider interface.
 */
@Component("synologyPhotoProvider")
@ConditionalOnProperty(name = ["photos.provider"], havingValue = "synology")
class SynologyPhotoProvider(
    private val sessionService: SynologySessionService,
    private val albumService: SynologyAlbumService,
    private val photoFetchService: SynologyPhotoFetchService
) : PhotoProvider {

    private val logger = LoggerFactory.getLogger(SynologyPhotoProvider::class.java)
    
    override fun getPhotos(limit: Int?): List<Photo> {
        logger.debug("Retrieving photos from Synology, limit: {}", limit)

        val sid = sessionService.getValidSessionId()

        val albumsResponse = albumService.fetchAlbums(sid)
        val albums = albumService.filterAlbums(albumsResponse)

        if (albums.isEmpty()) throw PhotosRetrievalFailedException("Synology provider: no albums matched configured filter")

        val allPhotos = photoFetchService.retrieveAllPhotosFromAlbums(sid, albums)
            .shuffled()

        val result = if (limit != null) allPhotos.take(limit.coerceAtLeast(0)) else allPhotos
        logger.debug("Retrieved ${result.size} photos (from total ${allPhotos.size}) from Synology")

        return result
    }

    override fun getPhotosCount(): Int {
        logger.debug("Getting photos count from Synology")
        val sid = sessionService.getValidSessionId()
        val albumsResponse = albumService.fetchAlbums(sid)
        val albums = albumService.filterAlbums(albumsResponse)
        var totalCount = 0
        for (album in albums) {
            val photos = photoFetchService.retrievePhotosFromAlbum(sid, album, null)
            totalCount += photos.size
        }
        return totalCount
    }

}


