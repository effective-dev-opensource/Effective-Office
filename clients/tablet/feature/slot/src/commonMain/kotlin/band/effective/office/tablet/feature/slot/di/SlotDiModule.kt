package band.effective.office.tablet.feature.slot.di

import band.effective.office.tablet.feature.slot.domain.usecase.GetSlotsByRoomUseCase
import band.effective.office.tablet.feature.slot.presentation.mapper.SlotUiMapper
import org.koin.dsl.module

val slotDiModule = module {
    single { SlotUiMapper() }
    single { GetSlotsByRoomUseCase(get()) }
}