package band.effective.office.tv.di

import band.effective.office.tv.core.data.di.dataModule
import band.effective.office.tv.core.ui.di.imageLoaderModule
import band.effective.office.tv.environment.Environment
import band.effective.office.tv.feature.events.di.eventsModule
import band.effective.office.tv.feature.photos.di.photosModule
import band.effective.office.tv.feature.stories.di.storiesModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class KoinInitializer {

    fun init(
        environment: Environment,
        appDeclaration: KoinApplication.() -> Unit =  {}
    ) {
        startKoin {
            modules(
                appModule(environment),
                dataModule,
                imageLoaderModule,
                photosModule,
                storiesModule,
                eventsModule,
                platformModule(),
            )
            appDeclaration()
        }
    }

    fun stop() {
        stopKoin()
    }
}


