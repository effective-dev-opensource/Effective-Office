package band.effective.office.tablet.feature.fastBooking.di

import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.feature.fastBooking.presentation.FastBookingViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val fastBookingModule = module {
    viewModel { (minEventDuration: Int, selectedRoom: RoomInfo, rooms: List<RoomInfo>) ->
        FastBookingViewModel(
            selectRoomUseCase = get(),
            createFastBookingUseCase = get(),
            deleteBookingUseCase = get(),
            timerUseCase = get(),
            minEventDuration = minEventDuration,
            selectedRoom = selectedRoom,
            rooms = rooms,
        )
    }
}
