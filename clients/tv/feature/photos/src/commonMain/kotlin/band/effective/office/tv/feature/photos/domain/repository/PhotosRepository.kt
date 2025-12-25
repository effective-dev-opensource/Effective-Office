package band.effective.office.tv.feature.photos.domain.repository

import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.ErrorResponse
import band.effective.office.tv.feature.photos.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotosRepository {
    suspend fun getPhotos(limit: Int = DEFAULT_LIMIT): Flow<Either<ErrorResponse, Photo>>

    companion object {
        const val DEFAULT_LIMIT = 30
    }
}

