package band.effective.office.tablet.core.data.di

import band.effective.office.tablet.core.data.api.BookingApi
import band.effective.office.tablet.core.data.api.Collector
import band.effective.office.tablet.core.data.api.UserApi
import band.effective.office.tablet.core.data.api.WorkspaceApi
import band.effective.office.tablet.core.data.api.impl.BookingApiImpl
import band.effective.office.tablet.core.data.api.impl.UserApiImpl
import band.effective.office.tablet.core.data.api.impl.WorkspaceApiImpl
import band.effective.office.tablet.core.data.mapper.EventInfoMapper
import band.effective.office.tablet.core.data.mapper.RoomInfoMapper
import band.effective.office.shared.core.network.HttpClientProvider
import band.effective.office.tablet.core.data.repository.BookingRepositoryImpl
import band.effective.office.tablet.core.data.repository.LocalBookingRepositoryImpl
import band.effective.office.tablet.core.data.repository.LocalRoomRepositoryImpl
import band.effective.office.tablet.core.data.repository.OrganizerRepositoryImpl
import band.effective.office.tablet.core.data.repository.RoomRepositoryImpl
import band.effective.office.tablet.core.data.utils.Buffer
import band.effective.office.tablet.core.domain.repository.BookingRepository
import band.effective.office.tablet.core.domain.repository.LocalBookingRepository
import band.effective.office.tablet.core.domain.repository.LocalRoomRepository
import band.effective.office.tablet.core.domain.repository.OrganizerRepository
import band.effective.office.tablet.core.domain.repository.RoomRepository
import org.koin.dsl.module

/**
 * Koin module for the data layer
 */
val dataModule = module {
    // Network
    single { HttpClientProvider }

    // Collectors
    single { Collector("") }

    // Mappers
    single { EventInfoMapper() }
    single { RoomInfoMapper(get()) }

    // API implementations
    single<BookingApi> { BookingApiImpl(get()) }

    single<UserApi> { UserApiImpl(get()) }

    single<WorkspaceApi> { WorkspaceApiImpl(get()) }

    single<OrganizerRepository> {
        OrganizerRepositoryImpl(api = get())
    }

    single<LocalBookingRepository> {
        LocalBookingRepositoryImpl(buffer = get())
    }

    single<BookingRepository> {
        BookingRepositoryImpl(
            api = get(),
            eventInfoMapper = get(),
        )
    }

    single<LocalRoomRepository> {
        LocalRoomRepositoryImpl(buffer = get())
    }

    single<RoomRepository> {
        RoomRepositoryImpl(
            api = get(),
            workspaceApi = get(),
            roomInfoMapper = get(),
        )
    }

    single { Buffer() }
}
