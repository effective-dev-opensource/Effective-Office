package band.effective.office.tv.feature.stories.di

import band.effective.office.tv.feature.stories.data.api.StoriesApi
import band.effective.office.tv.feature.stories.data.api.impl.StoriesApiImpl
import band.effective.office.tv.feature.stories.data.repository.ClockifyRepositoryImpl
import band.effective.office.tv.feature.stories.data.repository.DuolingoRepositoryImpl
import band.effective.office.tv.feature.stories.data.repository.NotionRepositoryImpl
import band.effective.office.tv.feature.stories.data.repository.SupernovaRepositoryImpl
import band.effective.office.tv.feature.stories.domain.repository.ClockifyRepository
import band.effective.office.tv.feature.stories.domain.repository.DuolingoRepository
import band.effective.office.tv.feature.stories.domain.repository.NotionRepository
import band.effective.office.tv.feature.stories.domain.repository.SupernovaRepository
import band.effective.office.tv.feature.stories.domain.service.StoriesDataProvider
import org.koin.dsl.module

/**
 * Koin module for Stories feature.
 * Provides API, repositories and domain services.
 */
val storiesModule = module {
    // Data Layer - API
    single<StoriesApi> { StoriesApiImpl() }

    // Data Layer - Repositories
    single<NotionRepository> { NotionRepositoryImpl(get()) }
    single<DuolingoRepository> { DuolingoRepositoryImpl(get()) }
    single<ClockifyRepository> { ClockifyRepositoryImpl(get()) }
    single<SupernovaRepository> { SupernovaRepositoryImpl(get()) }

    // Domain Layer - Services
    single { StoriesDataProvider(get(), get(), get(), get()) }
}

