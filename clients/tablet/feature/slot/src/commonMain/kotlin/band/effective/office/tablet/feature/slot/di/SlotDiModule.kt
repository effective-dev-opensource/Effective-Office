package band.effective.office.tablet.feature.slot.di

import band.effective.office.tablet.core.domain.useCase.RoomInfoUseCase
import band.effective.office.tablet.core.domain.useCase.TimerUseCase
import band.effective.office.tablet.feature.slot.domain.usecase.GetSlotsByRoomUseCase
import band.effective.office.tablet.feature.slot.presentation.SlotComponent
import band.effective.office.tablet.feature.slot.presentation.SlotComponentFactory
import band.effective.office.tablet.feature.slot.presentation.mapper.SlotUiMapper
import org.koin.dsl.module

val slotDiModule = module {
    single { SlotUiMapper() }
    single { GetSlotsByRoomUseCase(get()) }

    single<SlotComponentFactory> {
        val roomInfoUseCase = get<RoomInfoUseCase>()
        val timerUseCase = get<TimerUseCase>()
        val getSlotsByRoomUseCase = get<GetSlotsByRoomUseCase>()
        val slotUiMapper = get<SlotUiMapper>()
        SlotComponentFactory { coroutineScope, roomName, openBookingDialog ->
            SlotComponent(
                coroutineScope = coroutineScope,
                roomName = roomName,
                openBookingDialog = openBookingDialog,
                roomInfoUseCase = roomInfoUseCase,
                timerUseCase = timerUseCase,
                getSlotsByRoomUseCase = getSlotsByRoomUseCase,
                slotUiMapper = slotUiMapper,
            )
        }
    }
}