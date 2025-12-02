package band.effective.office.tv.di

import band.effective.office.tv.core.data.di.dataModule
import band.effective.office.tv.environment.Environment
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.dsl.module

class KoinInitializer {

    fun init(environment: Environment) {
        startKoin {
            modules(
                appModule(environment),
                dataModule,
            )
        }
    }

    fun stop() {
        stopKoin()
    }
}

fun appModule(environment: Environment) = module {
    single { environment }
    single(qualifier = named("ApiUrl")) { environment.apiUrl }
    single<String>(qualifier = named("ApiKey")) { environment.apiKey }
    single<Boolean>(qualifier = named("IsDebug")) { environment.isDebug }
}

