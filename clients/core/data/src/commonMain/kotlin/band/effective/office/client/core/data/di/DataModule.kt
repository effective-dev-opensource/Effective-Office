package band.effective.office.client.core.data.di

import band.effective.office.client.core.data.api.BookingApi
import band.effective.office.client.core.data.api.Collector
import band.effective.office.client.core.data.api.UserApi
import band.effective.office.client.core.data.api.WorkspaceApi
import band.effective.office.client.core.data.api.impl.BookingApiImpl
import band.effective.office.client.core.data.api.impl.UserApiImpl
import band.effective.office.client.core.data.api.impl.WorkspaceApiImpl
import band.effective.office.client.core.data.network.HttpClientProvider
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Koin module for the data layer
 */
val dataModule = module {
    // Network
    single { HttpClientProvider }

    // Collectors
    factory { Collector("") }

    // API implementations
    single<BookingApi> {
        BookingApiImpl(baseUrl = get(named("ApiUrl")))
    }

    single<UserApi> {
        UserApiImpl(baseUrl = get(named("ApiUrl")))
    }

    single<WorkspaceApi> {
        WorkspaceApiImpl(baseUrl = get(named("ApiUrl")))
    }
}
