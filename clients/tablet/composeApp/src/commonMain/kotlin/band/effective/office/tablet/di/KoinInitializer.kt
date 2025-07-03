package band.effective.office.tablet.di

import band.effective.office.tablet.core.data.di.dataModule
import band.effective.office.tablet.core.domain.di.domainModule
import band.effective.office.tablet.feature.bookingEditor.di.bookingEditorModule
import band.effective.office.tablet.feature.main.di.mainScreenModule
import band.effective.office.tablet.feature.slot.di.slotDiModule
import org.koin.core.context.startKoin

class KoinInitializer {
    fun init() {
        startKoin {
            modules(
                appModule,
                firebaseTopicsModule,
                dataModule,
                domainModule,
                mainScreenModule,
                bookingEditorModule,
                slotDiModule,
            )
        }
    }
}
