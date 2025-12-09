package band.effective.office.tv.core.data.di

import band.effective.office.shared.core.network.HttpClientProvider
import io.ktor.client.HttpClient
import org.koin.dsl.module

/**
 * Koin module for data layer.
 * Uses shared/core HttpClientProvider with standard settings.
 */
val dataModule = module {
    // Network
    single { HttpClientProvider }
    single<HttpClient> { HttpClientProvider.create() }
}


