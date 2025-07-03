package band.effective.office.tablet.feature.main.di

import band.effective.office.tablet.feature.main.domain.CreateBookingUseCase
import band.effective.office.tablet.feature.main.domain.DeleteBookingUseCase
import band.effective.office.tablet.feature.main.domain.FreeUpRoomUseCase
import band.effective.office.tablet.feature.main.domain.GetRoomIndexUseCase
import band.effective.office.tablet.feature.main.domain.GetSlotsByRoomUseCase
import band.effective.office.tablet.feature.main.domain.GetTimeToNextEventUseCase
import band.effective.office.tablet.feature.main.domain.UpdateBookingUseCase
import band.effective.office.tablet.feature.main.domain.mapper.EventInfoMapper
import band.effective.office.tablet.feature.main.domain.mapper.UpdateEventComponentStateToEventInfoMapper
import band.effective.office.tablet.feature.main.presentation.mapper.SlotUiMapper
import org.koin.dsl.module

val mainScreenModule = module {
    single { GetRoomIndexUseCase(get()) }
    single { GetTimeToNextEventUseCase() }
    single { GetSlotsByRoomUseCase(get()) }
    single { FreeUpRoomUseCase(get()) }
    single { CreateBookingUseCase(get()) }
    single { DeleteBookingUseCase(get()) }
    single { UpdateBookingUseCase(get()) }

    single { SlotUiMapper() }
    single { EventInfoMapper() }
    single { UpdateEventComponentStateToEventInfoMapper() }
}