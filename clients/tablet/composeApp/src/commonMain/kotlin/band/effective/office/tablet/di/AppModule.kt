package band.effective.office.tablet.di

import band.effective.office.tablet.BuildKonfig
import band.effective.office.tablet.isDebug
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    single(qualifier = named("ApiUrl")) {
        if (isDebug) BuildKonfig.API_URL_DEBUG else BuildKonfig.API_URL_RELEASE
    }
    single<String>(qualifier = named("ApiKey")) {
        BuildKonfig.API_KEY
    }
}