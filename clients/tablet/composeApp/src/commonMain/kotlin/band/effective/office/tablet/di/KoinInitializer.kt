package band.effective.office.tablet.di

import band.effective.office.tablet.core.data.di.dataModule
import band.effective.office.tablet.core.domain.di.domainModule
import band.effective.office.tablet.feature.bookingEditor.di.bookingEditorModule
import band.effective.office.tablet.feature.fastBooking.di.fastBookingModule
import band.effective.office.tablet.feature.main.di.mainScreenModule
import band.effective.office.tablet.feature.settings.di.settingsModule
import band.effective.office.tablet.feature.slot.di.slotDiModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module

class KoinInitializer {
    fun init(platformModules: List<Module> = emptyList()) {
        startKoin {
            modules(
                appModule,
                dataModule,
                domainModule,
                mainScreenModule,
                settingsModule,
                bookingEditorModule,
                fastBookingModule,
                slotDiModule,
                *platformModules.toTypedArray(),
            )
        }
    }
}
