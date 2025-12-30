package band.effective.office.tv.feature.photos.di

import band.effective.office.tv.feature.photos.data.api.PhotosApi
import band.effective.office.tv.feature.photos.data.api.impl.PhotosApiImpl
import band.effective.office.tv.feature.photos.data.repository.PhotosRepositoryImpl
import band.effective.office.tv.feature.photos.domain.repository.PhotosRepository
import org.koin.dsl.module

/**
 * Koin module for Photos feature.
 */
val photosModule = module {
    single<PhotosApi> { PhotosApiImpl(get()) }
    single<PhotosRepository> { PhotosRepositoryImpl(get()) }
}

