package band.effective.office.tablet.core.domain.di

import band.effective.office.tablet.core.domain.useCase.CheckBookingUseCase
import band.effective.office.tablet.core.domain.useCase.CheckSettingsUseCase
import band.effective.office.tablet.core.domain.useCase.CreateBookingUseCase
import band.effective.office.tablet.core.domain.useCase.OrganizersInfoUseCase
import band.effective.office.tablet.core.domain.useCase.RoomInfoUseCase
import band.effective.office.tablet.core.domain.useCase.SelectRoomUseCase
import band.effective.office.tablet.core.domain.useCase.SetRoomUseCase
import band.effective.office.tablet.core.domain.useCase.SlotUseCase
import band.effective.office.tablet.core.domain.useCase.TimerUseCase
import band.effective.office.tablet.core.domain.useCase.UpdateBookingUseCase
import band.effective.office.tablet.core.domain.useCase.UpdateUseCase
import org.koin.dsl.module

val domainModule = module {
    // Use cases
    single { RoomInfoUseCase(eventManager = get()) }
    single { OrganizersInfoUseCase(repository = get()) }
    single { CheckBookingUseCase(roomInfoUseCase = get()) }
    single { CheckSettingsUseCase() }
    single { SelectRoomUseCase() }
    single { SetRoomUseCase() }
    single { SlotUseCase() }
    single { TimerUseCase() }
    single { UpdateUseCase(roomInfoUseCase = get(), timerUseCase = get()) }

    single { CreateBookingUseCase(get()) }
    single { UpdateBookingUseCase(get()) }
}