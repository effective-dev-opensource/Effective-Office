package band.effective.office.tv.di

import band.effective.office.tv.BuildKonfig
import band.effective.office.tv.environment.Environment
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun appModule(environment: Environment) = module {
    single { environment }
    single<Boolean>(qualifier = named("IsDebug")) { environment.isDebug }

    single(qualifier = named("ApiUrl")) {
        if (environment.isDebug) BuildKonfig.API_URL_DEBUG else BuildKonfig.API_URL_RELEASE
    }

    single<String>(qualifier = named("ApiKey")) { BuildKonfig.API_KEY }
}
