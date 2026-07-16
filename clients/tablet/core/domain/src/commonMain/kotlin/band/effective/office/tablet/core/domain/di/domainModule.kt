package band.effective.office.tablet.core.domain.di

import band.effective.office.tablet.core.domain.model.SettingsManager
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
import band.effective.office.tablet.core.domain.useCase.RefreshDataUseCase
import band.effective.office.tablet.core.domain.useCase.ResourceDisposerUseCase
import band.effective.office.tablet.core.domain.useCase.RoomInfoUseCase
import band.effective.office.tablet.core.domain.useCase.SelectRoomUseCase
import band.effective.office.tablet.core.domain.useCase.SetRoomUseCase
import band.effective.office.tablet.core.domain.useCase.SlotUseCase
import band.effective.office.tablet.core.domain.useCase.TimerUseCase
import band.effective.office.tablet.core.domain.useCase.UpdateBookingUseCase
import band.effective.office.tablet.core.domain.useCase.UpdateUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

expect fun settingsStoreModule(): Module

val domainModule = module {

    includes(settingsStoreModule())

    single { SettingsManager(settings = get()) }

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
    single { ResourceDisposerUseCase(networkRoomRepository = get(), refreshDataUseCase = get()) }

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

    // Other use cases
    single { OrganizersInfoUseCase(repository = get()) }
    single { CheckBookingUseCase(roomInfoUseCase = get()) }
    single { CheckSettingsUseCase(settingsManager = get()) }
    single { SelectRoomUseCase() }
    single { SetRoomUseCase(settingsManager = get()) }
    single { SlotUseCase() }
    single { TimerUseCase() }
    single { UpdateUseCase(roomInfoUseCase = get(), timerUseCase = get()) }
}
