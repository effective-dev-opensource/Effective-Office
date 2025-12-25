package band.effective.office.tv.feature.photos.data.api.impl

import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.ErrorResponse
import band.effective.office.shared.core.network.HttpClientProvider
import band.effective.office.tv.core.data.network.get
import band.effective.office.tv.feature.photos.data.api.PhotosApi
import band.effective.office.tv.feature.photos.data.dto.PhotosResponseDTO

/**
 * Implementation of [PhotosApi] using shared HTTP stack.
 */
class PhotosApiImpl : PhotosApi {

    private val client = HttpClientProvider.create()

    override suspend fun getPhotos(limit: Int): Either<ErrorResponse, PhotosResponseDTO> =
        get(client, "api/v1/photos") {
            url.parameters.append("limit", limit.toString())
        }
}

