package band.effective.office.tablet.ui.mainScreen.mainScreen

import android.os.Build
import androidx.annotation.RequiresApi
import band.effective.office.network.model.Either
import band.effective.office.tablet.domain.model.EventInfo
import band.effective.office.tablet.domain.model.RoomInfo
import band.effective.office.tablet.domain.model.Slot
import band.effective.office.tablet.network.repository.impl.EventManager
import band.effective.office.tablet.ui.fastEvent.FastEventComponent
import band.effective.office.tablet.ui.freeSelectRoom.FreeSelectRoomComponent
import band.effective.office.tablet.ui.mainScreen.mainScreen.store.MainFactory
import band.effective.office.tablet.ui.mainScreen.mainScreen.store.MainStore
import band.effective.office.tablet.ui.mainScreen.slotComponent.SlotComponent
import band.effective.office.tablet.ui.mainScreen.slotComponent.store.SlotStore
import band.effective.office.tablet.ui.modal.ModalWindow
import band.effective.office.tablet.ui.updateEvent.UpdateEventComponent
import band.effective.office.tablet.utils.componentCoroutineScope
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.mvikotlin.core.instancekeeper.getStore
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.labels
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.N)
class MainComponent(
    private val componentContext: ComponentContext,
    private val storeFactory: StoreFactory,
    val onSettings: () -> Unit
) : ComponentContext by componentContext, KoinComponent {

    private val eventManager: EventManager by inject()

    private var currentRoomName: String = {
        state.value.let { state ->
            if (state.roomList.isNotEmpty()) {
                state.roomList[state.indexSelectRoom].name
            } else {
                RoomInfo.defaultValue.name
            }
        }
    }.toString()

    val slotComponent = SlotComponent(
        componentContext = componentContext,
        storeFactory = storeFactory,
        roomName = { currentRoomName },
        openBookingDialog = { event, room ->
            mainStore.accept(
                intent = MainStore.Intent.OnChangeEventRequest(
                    eventInfo = event
                )
            )
        }
    )

    private val navigation = SlotNavigation<ModalWindowsConfig>()

    @RequiresApi(Build.VERSION_CODES.O)
    val modalWindowSlot = childSlot(
        source = navigation,
        childFactory = ::childFactory
    )

    fun openModalWindow(dist: ModalWindowsConfig) {
        navigation.activate(dist)
    }

    fun closeModalWindow() {
        navigation.dismiss()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun childFactory(
        modalWindows: ModalWindowsConfig,
        componentContext: ComponentContext
    ): ModalWindow {
        return when (modalWindows) {
            is ModalWindowsConfig.FreeRoom -> FreeSelectRoomComponent(
                componentContext = componentContext,
                storeFactory = storeFactory,
                eventInfo = modalWindows.event,
                onRemoveEvent = { event ->
                    this.componentContext.componentCoroutineScope().launch {
                        eventManager.deleteBooking(
                            roomName = state.value.run { roomList[indexSelectRoom].name },
                            eventInfo = event
                        )
                    }
                },
                onCloseRequest = { closeModalWindow() })

            is ModalWindowsConfig.UpdateEvent -> UpdateEventComponent(
                componentContext = componentContext,
                storeFactory = storeFactory,
                event = modalWindows.event,
                room = modalWindows.room,
                onDelete = { slot ->
                    slotComponent.sendIntent(SlotStore.Intent.Delete(slot) {
                        this.componentContext.componentCoroutineScope().launch {
                            (slot as? Slot.EventSlot)?.eventInfo?.apply {
                                eventManager.deleteBooking(
                                    eventInfo = this,
                                    roomName = state.value.run { roomList[indexSelectRoom].name }
                                )
                            }
                        }
                    })
                },
                onCloseRequest = { closeModalWindow() },
                onEventCreation = { eventInfo ->
                    this.componentContext.componentCoroutineScope().launch {
                        val roomName = modalWindows.room
                        val result = eventManager.createBooking(
                            eventInfo = eventInfo,
                            roomName = roomName
                        )
                        when (result) {
                            is Either.Success -> {
                                mainStore.accept(
                                    MainStore.Intent.OnShowToast(
                                        "Событие создано успешно"
                                    )
                                )
                            }

                            is Either.Error -> {
                                mainStore.accept(
                                    MainStore.Intent.OnShowToast(
                                        "Ошибка при создании события: ${result.error}"
                                    )
                                )
                            }
                        }
                    }
                },
                onEventUpdate = { eventInfo ->
                    this.componentContext.componentCoroutineScope().launch {
                        val roomName = modalWindows.room
                        val result = eventManager.updateBooking(
                            eventInfo = eventInfo,
                            roomName = roomName
                        )
                    }
                }
            )

            is ModalWindowsConfig.FastEvent -> FastEventComponent(
                componentContext = componentContext,
                storeFactory = storeFactory,
                minEventDuration = modalWindows.minEventDuration,
                onEventCreation = { eventInfo, room ->
                    val result = this.componentContext.componentCoroutineScope().async {
                        eventManager.createBooking(
                            eventInfo = eventInfo,
                            roomName = room
                        )
                    }
                    return@FastEventComponent result.await()
                },
                onRemoveEvent = { event, room ->
                    val result = this.componentContext.componentCoroutineScope().async {
                        eventManager.deleteBooking(
                            roomName = room,
                            eventInfo = event
                        )
                    }
                    return@FastEventComponent result.await()
                },
                selectedRoom = modalWindows.selectedRoom,
                rooms = modalWindows.rooms,
                onCloseRequest = { closeModalWindow() }
            )
        }
    }


    private val mainStore = instanceKeeper.getStore {
        MainFactory(
            storeFactory = storeFactory,
            navigate = ::openModalWindow,
            updateRoomInfo = { roomInfo, date ->
                updateComponents(roomInfo, date)
            },
            updateDate = ::updateDate
        ).create()
    }

    private fun updateComponents(roomInfo: RoomInfo, date: Calendar) {
        slotComponent.sendIntent(
            SlotStore.Intent.UpdateRequest(
                room = roomInfo.name,
                refresh = false
            )
        )
        slotComponent.sendIntent(
            SlotStore.Intent.UpdateDate(date)
        )
    }

    private fun updateDate(date: Calendar) {
        slotComponent.sendIntent(
            SlotStore.Intent.UpdateDate(date)
        )
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val state = mainStore.stateFlow
    val label = mainStore.labels

    fun sendIntent(intent: MainStore.Intent) {
        mainStore.accept(intent)
    }

    sealed interface ModalWindowsConfig : Parcelable {
        @Parcelize
        data class UpdateEvent(
            val event: EventInfo,
            val room: String
        ) : ModalWindowsConfig

        @Parcelize
        data class FreeRoom(val event: EventInfo) : ModalWindowsConfig

        @Parcelize
        data class FastEvent(
            val minEventDuration: Int,
            val selectedRoom: RoomInfo,
            val rooms: List<RoomInfo>
        ) : ModalWindowsConfig
    }
}