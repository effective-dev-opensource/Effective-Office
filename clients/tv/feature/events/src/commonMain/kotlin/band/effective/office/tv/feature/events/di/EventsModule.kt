package band.effective.office.tv.feature.events.di

import band.effective.office.tv.feature.events.data.api.EventsApi
import band.effective.office.tv.feature.events.data.api.impl.EventsApiImpl
import band.effective.office.tv.feature.events.data.repository.EventsRepositoryImpl
import band.effective.office.tv.feature.events.domain.repository.EventsRepository
import org.koin.dsl.module

/**
 * Koin module for Events feature.
 */
val eventsModule = module {
    single<EventsApi> { EventsApiImpl(get()) }
    single<EventsRepository> { EventsRepositoryImpl(get()) }
}
