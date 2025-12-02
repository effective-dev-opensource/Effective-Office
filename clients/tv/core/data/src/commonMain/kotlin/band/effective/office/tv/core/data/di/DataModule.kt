package band.effective.office.tv.core.data.di

import band.effective.office.tv.core.data.network.HttpClientProvider
import io.ktor.client.HttpClient
import org.koin.dsl.module

/**
 * Koin module for data layer.
 */
val dataModule = module {
    // Network
    single { HttpClientProvider }
    single<HttpClient> { HttpClientProvider.create() }
}


