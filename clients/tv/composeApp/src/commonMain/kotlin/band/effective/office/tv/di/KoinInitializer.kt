package band.effective.office.tv.di

import band.effective.office.tv.core.data.di.dataModule
import band.effective.office.tv.environment.Environment
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

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


