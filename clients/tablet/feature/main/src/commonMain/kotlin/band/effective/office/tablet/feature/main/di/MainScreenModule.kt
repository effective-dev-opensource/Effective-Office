package band.effective.office.tablet.feature.main.di

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.feature.main.domain.GetRoomIndexUseCase
import band.effective.office.tablet.feature.main.domain.GetTimeToNextEventUseCase
import band.effective.office.tablet.feature.main.presentation.freeuproom.FreeSelectRoomViewModel
import band.effective.office.tablet.feature.main.presentation.main.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val mainScreenModule = module {
    single { GetRoomIndexUseCase(get()) }
    single { GetTimeToNextEventUseCase() }

    viewModelOf(::MainViewModel)
    viewModel { (event: EventInfo, room: String) ->
        FreeSelectRoomViewModel(deleteBookingUseCase = get(), eventInfo = event, roomName = room)
    }
}