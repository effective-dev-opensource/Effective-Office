package band.effective.office.tablet.feature.bookingEditor.di

import band.effective.office.tablet.feature.bookingEditor.presentation.mapper.EventInfoMapper
import band.effective.office.tablet.feature.bookingEditor.presentation.mapper.UpdateEventComponentStateToEventInfoMapper
import org.koin.dsl.module

val bookingEditorModule = module {
    single { EventInfoMapper() }
    single { UpdateEventComponentStateToEventInfoMapper() }
}