package band.effective.office.tablet.core.domain.di

import band.effective.office.tablet.core.domain.manager.DateResetManager
import band.effective.office.tablet.core.domain.platform.roomRefreshInterval
import band.effective.office.tablet.core.domain.useCase.CheckBookingUseCase
import band.effective.office.tablet.core.domain.useCase.CheckSettingsUseCase
import band.effective.office.tablet.core.domain.useCase.CreateBookingUseCase
import band.effective.office.tablet.core.domain.useCase.DeleteBookingUseCase
import band.effective.office.tablet.core.domain.useCase.GetCurrentRoomInfosUseCase
import band.effective.office.tablet.core.domain.useCase.GetEventsFlowUseCase
import band.effective.office.tablet.core.domain.useCase.GetRoomByNameUseCase
import band.effective.office.tablet.core.domain.useCase.GetRoomNamesUseCase
import band.effective.office.tablet.core.domain.useCase.GetRoomsInfoUseCase
import band.effective.office.tablet.core.domain.useCase.OrganizersInfoUseCase
import band.effective.office.tablet.core.domain.useCase.PeriodicRoomRefreshUseCase
import band.effective.office.tablet.core.domain.useCase.RefreshDataUseCase
import band.effective.office.tablet.core.domain.useCase.ResourceDisposerUseCase
import band.effective.office.tablet.core.domain.useCase.RoomInfoUseCase
import band.effective.office.tablet.core.domain.useCase.SelectRoomUseCase
import band.effective.office.tablet.core.domain.useCase.SetRoomUseCase
import band.effective.office.tablet.core.domain.useCase.SlotUseCase
import band.effective.office.tablet.core.domain.useCase.TimerUseCase
import band.effective.office.tablet.core.domain.useCase.UpdateBookingUseCase
import band.effective.office.tablet.core.domain.useCase.UpdateUseCase
import org.koin.dsl.module

val domainModule = module {
    // Booking use cases
    single {
        CreateBookingUseCase(
            networkBookingRepository = get(),
            localBookingRepository = get(),
            getRoomByNameUseCase = get(),
        )
    }
    single {
        UpdateBookingUseCase(
            networkBookingRepository = get(),
            localBookingRepository = get(),
            getRoomByNameUseCase = get(),
        )
    }
    single {
        DeleteBookingUseCase(
            networkBookingRepository = get(),
            localBookingRepository = get(),
            getRoomByNameUseCase = get(),
        )
    }

    // Room information use cases
    single { GetEventsFlowUseCase(localRoomRepository = get()) }
    single { RefreshDataUseCase(networkRoomRepository = get(), localRoomRepository = get()) }
    single { GetCurrentRoomInfosUseCase(localRoomRepository = get()) }
    single { GetRoomByNameUseCase(localRoomRepository = get()) }
    single { GetRoomsInfoUseCase(localRoomRepository = get(), refreshDataUseCase = get()) }
    single { GetRoomNamesUseCase(getRoomsInfoUseCase = get()) }
    single {
        PeriodicRoomRefreshUseCase(
            refreshDataUseCase = get(),
            interval = roomRefreshInterval,
        )
    }
    single {
        ResourceDisposerUseCase(
            networkRoomRepository = get(),
            refreshDataUseCase = get(),
            periodicRoomRefreshUseCase = get(),
        )
    }

    single {
        RoomInfoUseCase(
            getRoomNamesUseCase = get(),
            refreshDataUseCase = get(),
            getRoomsInfoUseCase = get(),
            getEventsFlowUseCase = get(),
            getRoomByNameUseCase = get(),
            getCurrentRoomInfosUseCase = get(),
        )
    }

    single { DateResetManager() }

    // Other use cases
    single { OrganizersInfoUseCase(repository = get()) }
    single { CheckBookingUseCase(roomInfoUseCase = get()) }
    single { CheckSettingsUseCase() }
    single { SelectRoomUseCase() }
    single { SetRoomUseCase() }
    single { SlotUseCase() }
    single { TimerUseCase() }
    single { UpdateUseCase(roomInfoUseCase = get(), timerUseCase = get()) }
}
