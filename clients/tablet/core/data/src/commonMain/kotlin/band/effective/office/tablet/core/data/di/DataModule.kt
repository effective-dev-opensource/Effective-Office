package band.effective.office.tablet.core.data.di

import band.effective.office.tablet.core.data.api.BookingApi
import band.effective.office.tablet.core.data.api.Collector
import band.effective.office.tablet.core.data.api.UserApi
import band.effective.office.tablet.core.data.api.WorkspaceApi
import band.effective.office.tablet.core.data.api.impl.BookingApiImpl
import band.effective.office.tablet.core.data.api.impl.UserApiImpl
import band.effective.office.tablet.core.data.api.impl.WorkspaceApiImpl
import band.effective.office.tablet.core.data.network.HttpClientProvider
import band.effective.office.tablet.core.data.repository.EventManager
import band.effective.office.tablet.core.data.repository.LocalEventStoreRepository
import band.effective.office.tablet.core.data.repository.NetworkEventRepository
import band.effective.office.tablet.core.data.repository.OrganizerRepositoryImpl
import band.effective.office.tablet.core.domain.repository.BookingRepository
import band.effective.office.tablet.core.domain.repository.EventManagerRepository
import band.effective.office.tablet.core.domain.repository.LocalBookingRepository
import band.effective.office.tablet.core.domain.repository.OrganizerRepository
import org.koin.dsl.module

/**
 * Koin module for the data layer
 */
val dataModule = module {
    // Network
    single { HttpClientProvider }

    // Collectors
    single { Collector("") }

    // API implementations
    single<BookingApi> { BookingApiImpl(get()) }

    single<UserApi> { UserApiImpl(get()) }

    single<WorkspaceApi> { WorkspaceApiImpl(get()) }

    // Repository implementations
    single<OrganizerRepository> {
        OrganizerRepositoryImpl(api = get())
    }

    single<BookingRepository> {
        NetworkEventRepository(api = get(), workspaceApi = get())
    }

    single<LocalBookingRepository> {
        LocalEventStoreRepository()
    }

    single<EventManagerRepository> {
        EventManager(networkEventRepository = get(), localEventStoreRepository = get())
    }
}
