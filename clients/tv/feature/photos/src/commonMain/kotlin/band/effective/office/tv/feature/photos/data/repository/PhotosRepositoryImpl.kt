package band.effective.office.tv.feature.photos.data.repository

import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.ErrorResponse
import band.effective.office.tv.feature.photos.data.api.PhotosApi
import band.effective.office.tv.feature.photos.data.mapper.PhotoMapper
import band.effective.office.tv.feature.photos.domain.model.Photo
import band.effective.office.tv.feature.photos.domain.repository.PhotosRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flowOf

class PhotosRepositoryImpl(
    private val api: PhotosApi,
) : PhotosRepository {

    override suspend fun getPhotos(limit: Int): Flow<Either<ErrorResponse, Photo>> {
        return when (val result = api.getPhotos(limit)) {
            is Either.Success -> {
                result.data.data?.photos.orEmpty()
                    .mapNotNull { PhotoMapper.toDomain(it) }
                    .map { Either.Success(it) }
                    .asFlow()
            }
            is Either.Error -> flowOf(Either.Error(result.error))
        }
    }
}


