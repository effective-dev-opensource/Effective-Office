package band.effective.office.tablet.feature.main.di

import band.effective.office.tablet.feature.main.domain.CurrentTimeHolder
import band.effective.office.tablet.feature.main.domain.GetRoomIndexUseCase
import band.effective.office.tablet.feature.main.domain.GetTimeToNextEventUseCase
import band.effective.office.tablet.feature.main.domain.RefreshOnTimeZoneChangeUseCase
import org.koin.dsl.module

val mainScreenModule = module {
    single { CurrentTimeHolder() }
    single { GetRoomIndexUseCase(get()) }
    single { GetTimeToNextEventUseCase() }
    single {
        RefreshOnTimeZoneChangeUseCase(
            roomInfoUseCase = get(),
            currentTimeHolder = get(),
        )
    }
}