package band.effective.office.tablet.feature.main.di

import band.effective.office.tablet.feature.main.domain.GetRoomIndexUseCase
import band.effective.office.tablet.feature.main.domain.GetSlotsByRoomUseCase
import band.effective.office.tablet.feature.main.domain.GetTimeToNextEventUseCase
import band.effective.office.tablet.feature.main.domain.mapper.SlotUiMapper
import org.koin.dsl.module

val mainScreenModule = module {
    single { GetRoomIndexUseCase(get()) }
    single { GetTimeToNextEventUseCase() }
    single { GetSlotsByRoomUseCase(get()) }

    single { SlotUiMapper() }
}