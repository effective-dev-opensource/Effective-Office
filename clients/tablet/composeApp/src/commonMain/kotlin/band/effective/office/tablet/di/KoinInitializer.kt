package band.effective.office.tablet.di

import band.effective.office.client.core.data.di.dataModule
import org.koin.core.context.startKoin

class KoinInitializer {
    fun init() {
        startKoin {
            modules(
                appModule,
                dataModule
            )
        }
    }
}
