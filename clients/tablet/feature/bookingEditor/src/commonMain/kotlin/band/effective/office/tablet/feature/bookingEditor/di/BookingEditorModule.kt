package band.effective.office.tablet.feature.bookingEditor.di

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.useCase.CheckBookingUseCase
import band.effective.office.tablet.feature.bookingEditor.presentation.BookingEditorViewModel
import band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.DateTimePickerComponent
import band.effective.office.tablet.feature.bookingEditor.presentation.datetimepicker.DateTimePickerComponentFactory
import band.effective.office.tablet.feature.bookingEditor.presentation.mapper.EventInfoMapper
import band.effective.office.tablet.feature.bookingEditor.presentation.mapper.UpdateEventComponentStateToEventInfoMapper
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val bookingEditorModule = module {
    single { EventInfoMapper() }
    single { UpdateEventComponentStateToEventInfoMapper() }

    single<DateTimePickerComponentFactory> {
        val checkBookingUseCase = get<CheckBookingUseCase>()
        DateTimePickerComponentFactory { coroutineScope, onSelectDate, onCloseRequest, event, room, duration, initDate ->
            DateTimePickerComponent(
                coroutineScope = coroutineScope,
                onSelectDate = onSelectDate,
                onCloseRequest = onCloseRequest,
                event = event,
                room = room,
                duration = duration,
                initDate = initDate,
                checkBookingUseCase = checkBookingUseCase,
            )
        }
    }

    viewModel { (event: EventInfo, room: String) ->
        BookingEditorViewModel(
            organizersInfoUseCase = get(),
            checkBookingUseCase = get(),
            updateBookingUseCase = get(),
            createBookingUseCase = get(),
            deleteBookingUseCase = get(),
            eventInfoMapper = get(),
            stateToEventInfoMapper = get(),
            dateTimePickerComponentFactory = get(),
            initialEvent = event,
            roomName = room,
        )
    }
}
