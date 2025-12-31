package band.effective.office.tv.feature.photos.data.api

import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.ErrorResponse
import band.effective.office.tv.feature.photos.data.dto.PhotosResponseDTO

/**
 * API contract for Photos feature.
 */
interface PhotosApi {
    /**
     * Load photos from backend.
     *
     * @param limit max number of items to fetch.
     */
    suspend fun getPhotos(limit: Int): Either<ErrorResponse, PhotosResponseDTO>
}

