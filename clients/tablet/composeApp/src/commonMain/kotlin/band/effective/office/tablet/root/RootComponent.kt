package band.effective.office.tablet.root

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.model.Slot
import band.effective.office.tablet.core.domain.orchestrator.EventOrchestrator
import band.effective.office.tablet.core.domain.useCase.CheckSettingsUseCase
import band.effective.office.tablet.core.domain.useCase.ResourceDisposerUseCase
import band.effective.office.tablet.core.ui.common.ModalWindow
import band.effective.office.tablet.feature.bookingEditor.presentation.BookingEditorComponent
import band.effective.office.tablet.feature.fastBooking.presentation.FastBookingComponent
import band.effective.office.tablet.feature.main.presentation.freeuproom.FreeSelectRoomComponent
import band.effective.office.tablet.feature.main.presentation.main.MainComponent
import band.effective.office.tablet.feature.settings.SettingsComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RootComponent(
    componentContext: ComponentContext,
    private val eventOrchestrator: EventOrchestrator,
) : ComponentContext by componentContext, KoinComponent {

    private val navigation = StackNavigation<Config>()
    private val modalNavigation = SlotNavigation<ModalWindowsConfig>()

    private val resourceDisposerUseCase: ResourceDisposerUseCase by inject()
    private val checkSettingsUseCase: CheckSettingsUseCase by inject()

    val modalWindowSlot = childSlot(
        source = modalNavigation,
        childFactory = ::createModalWindow,
        serializer = ModalWindowsConfig.serializer(),
    )

    val childStack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        initialConfiguration = if (checkSettingsUseCase().isEmpty()) {
            Config.Settings
        } else {
            Config.Main
        },
        handleBackButton = true,
        serializer = kotlinx.serialization.serializer<Config>(),
        childFactory = ::createChild,
    )

    init {
        resourceDisposerUseCase()
    }

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): Child = when (config) {
        is Config.Main -> {
            Child.MainChild(
                MainComponent(
                    componentContext = componentContext,
                    onFastBooking = ::handleFastBookingIntent,
                    onOpenFreeRoomModal = ::handleFreeRoomIntent,
                    openBookingDialog = ::openBookingDialog,
                    eventOrchestrator = eventOrchestrator,
                )
            )
        }

        is Config.Settings -> {
            Child.SettingsChild(
                SettingsComponent(
                    componentContext = componentContext,
                    onExitApp = {
                        // TODO
                    },
                    onMainScreen = {
                        navigation.replaceAll(Config.Main)
                    },
                )
            )
        }
    }

    private fun openBookingDialog(event: EventInfo, room: String) {
        modalNavigation.activate(
            ModalWindowsConfig.UpdateEvent(
                event = event,
                room = room,
            )
        )
    }

    private fun handleFastBookingIntent(minDuration: Int, selectedRoom: RoomInfo, rooms: List<RoomInfo>) {
        modalNavigation.activate(
            ModalWindowsConfig.FastEvent(
                minEventDuration = minDuration,
                selectedRoom = selectedRoom,
                rooms = rooms
            )
        )
    }

    private fun handleFreeRoomIntent(currentEvent: EventInfo, roomName: String) {
        modalNavigation.activate(
            ModalWindowsConfig.FreeRoom(
                event = currentEvent,
                roomName = roomName
            )
        )
    }

    private fun createModalWindow(
        modalConfig: ModalWindowsConfig,
        componentContext: ComponentContext
    ): ModalWindow {
        return when (modalConfig) {
            is ModalWindowsConfig.FreeRoom -> createFreeRoomComponent(modalConfig, componentContext)
            is ModalWindowsConfig.UpdateEvent -> createBookingEditorComponent(
                modalConfig,
                componentContext
            )

            is ModalWindowsConfig.FastEvent -> createFastBookingComponent(
                modalConfig,
                componentContext
            )
        }
    }

    private fun createFreeRoomComponent(
        config: ModalWindowsConfig.FreeRoom,
        componentContext: ComponentContext
    ): FreeSelectRoomComponent {
        return FreeSelectRoomComponent(
            componentContext = componentContext,
            eventInfo = config.event,
            roomName = config.roomName,
            onCloseRequest = modalNavigation::dismiss,
        )
    }

    private fun createBookingEditorComponent(
        config: ModalWindowsConfig.UpdateEvent,
        componentContext: ComponentContext
    ): BookingEditorComponent {
        return BookingEditorComponent(
            componentContext = componentContext,
            initialEvent = config.event,
            roomName = config.room,
            onDeleteEvent = ::handleDeleteEvent,
            onCloseRequest = modalNavigation::dismiss,
        )
    }

    private fun handleDeleteEvent(slot: Slot) {
        val mainComponent = (childStack.value.active.instance as? Child.MainChild)?.component
        mainComponent?.handleDeleteEvent(slot)
    }

    private fun createFastBookingComponent(
        config: ModalWindowsConfig.FastEvent,
        componentContext: ComponentContext
    ): FastBookingComponent {
        return FastBookingComponent(
            componentContext = componentContext,
            minEventDuration = config.minEventDuration,
            selectedRoom = config.selectedRoom,
            rooms = config.rooms,
            onCloseRequest = modalNavigation::dismiss,
        )
    }

    sealed class Child {
        data class MainChild(val component: MainComponent) : Child()
        data class SettingsChild(val component: SettingsComponent) : Child()
    }

    @Serializable
    sealed class Config {
        @Serializable
        object Settings : Config()

        @Serializable
        object Main : Config()
    }

    @Serializable
    sealed class ModalWindowsConfig {
        @Serializable
        data class FreeRoom(val event: EventInfo, val roomName: String) : ModalWindowsConfig()

        @Serializable
        data class UpdateEvent(val event: EventInfo, val room: String) : ModalWindowsConfig()

        @Serializable
        data class FastEvent(
            val minEventDuration: Int,
            val selectedRoom: RoomInfo,
            val rooms: List<RoomInfo>
        ) : ModalWindowsConfig()
    }
}
