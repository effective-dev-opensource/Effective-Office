package band.effective.office.tablet.di

import band.effective.office.shared.core.network.ApiConfig
import band.effective.office.tablet.BuildKonfig
import band.effective.office.tablet.core.ui.inactivity.InactivityTracking
import band.effective.office.tablet.isDebug
import org.koin.dsl.module

val appModule = module {

    includes(timeReceiverModule())

    single { InactivityTracking() }

    single {
        ApiConfig(
            url = if (isDebug) BuildKonfig.API_URL_DEBUG else BuildKonfig.API_URL_RELEASE,
            key = BuildKonfig.API_KEY,
        )
    }
}
