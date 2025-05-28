package band.effective.office.tablet.ui.mainScreen.mainScreen.store

import android.os.Build
import androidx.annotation.RequiresApi
import band.effective.office.network.model.Either
import band.effective.office.tablet.domain.model.ErrorWithData
import band.effective.office.tablet.domain.model.RoomInfo
import band.effective.office.tablet.domain.useCase.CheckSettingsUseCase
import band.effective.office.tablet.domain.useCase.RoomInfoUseCase
import band.effective.office.tablet.domain.useCase.TimerUseCase
import band.effective.office.tablet.domain.useCase.UpdateUseCase
import band.effective.office.tablet.ui.mainScreen.mainScreen.MainComponent
import band.effective.office.tablet.utils.BootstrapperTimer
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Calendar
import java.util.GregorianCalendar
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class MainFactory(
    private val storeFactory: StoreFactory,
    private val navigate: (MainComponent.ModalWindowsConfig) -> Unit,
    private val updateRoomInfo: (RoomInfo, Calendar) -> Unit,
    private val updateDate: (Calendar) -> Unit
) : KoinComponent {

    private val roomInfoUseCase: RoomInfoUseCase by inject()
    private val checkSettingsUseCase: CheckSettingsUseCase by inject()
    private val updateUseCase: UpdateUseCase by inject()
    private val timerUseCase: TimerUseCase by inject()
    private val currentTimeTimer = BootstrapperTimer<Action>(timerUseCase)
    private val currentRoomTimer = BootstrapperTimer<Action>(timerUseCase)
    private val errorTimer = BootstrapperTimer<Action>(timerUseCase)

    @OptIn(ExperimentalMviKotlinApi::class)
    fun create(): MainStore =
        object : MainStore,
            Store<MainStore.Intent, MainStore.State, MainStore.Label> by storeFactory.create(
                name = "MainStore",
                initialState = MainStore.State.defaultState,
                bootstrapper = coroutineBootstrapper {
                    // get current room name from memory
                    launch {
                        if (checkSettingsUseCase().isEmpty()) {
                            dispatch(Action.OnSettings)
                        }
                    }

                    // initial load
                    launch {
                        val roomsInfo = roomInfoUseCase()
                        dispatch(Action.OnLoad(roomsInfo))
                    }

                    // update events when start/finish event in room
                    launch(Dispatchers.IO) {
                        updateUseCase.updateFlow().collect {
                            delay(1.seconds)
                            withContext(Dispatchers.Main) {
                                dispatch(Action.OnLoad(roomInfoUseCase()))
                            }
                        }
                    }
                    // update time to start next event or finish current event
                    timerUseCase.timer(this, 1.seconds) { _ ->
                        withContext(Dispatchers.Main) {
                            dispatch(Action.OnUpdateTimer)
                        }
                    }
                    // update events list
                    launch(Dispatchers.Main) {
                        roomInfoUseCase.subscribe().collect { roomsInfo ->
                            if (roomsInfo.isNotEmpty())
                                dispatch(Action.OnUpdateRoomInfo)
                        }
                    }
                    // reset selected room
                    currentRoomTimer.start(bootstrapperScope = this, delay = 1.minutes) {
                        withContext(Dispatchers.Main) {
                            dispatch(Action.OnLoad(roomInfoUseCase()))
                        }
                    }
                    // update cache when get error
                    errorTimer.init(this, 15.minutes) {
                        roomInfoUseCase.updateCache()
                        withContext(Dispatchers.Main) {
                            dispatch(Action.OnLoad(roomInfoUseCase()))
                        }
                    }
                    // reset select date
                    currentTimeTimer.start(this, 1.minutes) {
                        withContext(Dispatchers.Main) {
                            dispatch(Action.RefreshDate)
                        }
                    }
                },
                executorFactory = ::ExecutorImpl,
                reducer = ReducerImpl
            ) {}

    private sealed interface Message {
        data class Load(
            val isSuccess: Boolean,
            val roomList: List<RoomInfo>,
            val indexSelectRoom: Int
        ) : Message

        data class UpdateDisconnect(val newValue: Boolean) : Message
        data object Reboot : Message
        data object OnSettings : Message
        data class SelectRoom(val index: Int) : Message
        data object UpdateTimer : Message
        data class UpdateDate(val newDate: Calendar) : Message
        data class ShowToast(val text: String) : Message
    }

    private sealed interface Action {
        data class OnLoad(val roomInfos: Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>) :
            Action

        data object OnSettings : Action
        data object OnUpdateTimer : Action
        data object OnUpdateRoomInfo : Action
        data object RefreshDate : Action
    }

    private inner class ExecutorImpl() :
        CoroutineExecutor<MainStore.Intent, Action, MainStore.State, Message, MainStore.Label>() {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun executeIntent(intent: MainStore.Intent, getState: () -> MainStore.State) {
            when (intent) {
                is MainStore.Intent.OnOpenFreeRoomModal ->
                    navigate(MainComponent.ModalWindowsConfig.FreeRoom(getState().run { roomList[indexSelectRoom].currentEvent!! }))

                is MainStore.Intent.RebootRequest -> reboot(state = getState(), refresh = true)
                is MainStore.Intent.OnChangeEventRequest -> navigate(
                    MainComponent.ModalWindowsConfig.UpdateEvent(
                        event = intent.eventInfo,
                        room = getState().run { roomList[indexSelectRoom].name }
                    )
                )

                is MainStore.Intent.OnSelectRoom -> dispatch(Message.SelectRoom(intent.index.apply {
                    currentRoomTimer.restart()
                    getState().let { updateRoomInfo(it.roomList[this], it.selectDate) }
                }))

                MainStore.Intent.OnUpdate -> reboot(getState())
                is MainStore.Intent.OnFastBooking -> {
                    navigate(
                        MainComponent.ModalWindowsConfig.FastEvent(
                            minEventDuration = intent.minDuration,
                            selectedRoom = getState().run { roomList[indexSelectRoom] },
                            rooms = getState().run { roomList }
                        )
                    )
                }

                is MainStore.Intent.OnUpdateSelectDate -> {
                    currentTimeTimer.restart()
                    currentRoomTimer.restart()
                    val newDate = (getState().selectDate.clone() as Calendar).apply {
                        add(
                            Calendar.DAY_OF_YEAR,
                            intent.updateInDays
                        )
                    }
                    dispatch(Message.UpdateDate(newDate))
                    updateDate(newDate)
                }

                MainStore.Intent.OnResetSelectDate -> {
                    updateDate(GregorianCalendar())
                    dispatch(Message.UpdateDate(GregorianCalendar()))
                }

                is MainStore.Intent.OnShowToast -> {
                    dispatch(Message.ShowToast(intent.text))
                }
            }
        }

        fun reboot(
            state: MainStore.State,
            refresh: Boolean = false,
            resetSelectRoom: Boolean = true
        ) = scope.launch {
            val roomIndex = if (resetSelectRoom) state.indexRoom() else state.indexSelectRoom
            if (refresh) {
                if (!state.isData) {
                    dispatch(Message.Reboot)
                }

                roomInfoUseCase.updateCache()
            }
            when (val roomInfos = roomInfoUseCase()) {
                is Either.Success -> {
                    dispatch(Message.UpdateDisconnect(false))
                    dispatch(
                        Message.Load(
                            isSuccess = true,
                            roomList = roomInfos.data,
                            indexSelectRoom = roomIndex
                        )
                    )
                    updateRoomInfo(roomInfos.data[roomIndex], state.selectDate)
                }

                is Either.Error -> {
                    val save = roomInfos.error.saveData
                    if (!state.isData) {
                        dispatch(
                            Message.Load(
                                isSuccess = false,
                                roomList = save ?: listOf(RoomInfo.defaultValue),
                                indexSelectRoom = 0
                            )
                        )
                    } else {
                        Message.UpdateDisconnect(true)
                    }
                }
            }
        }

        override fun executeAction(action: Action, getState: () -> MainStore.State) {
            when (action) {
                is Action.OnLoad -> {
                    when (val roomInfos = action.roomInfos) {
                        is Either.Success -> {
                            val selectedRoomName = checkSettingsUseCase()
                            val roomIndex =
                                roomInfos.data.indexOfFirst { it.name == selectedRoomName }
                                    .coerceAtLeast(0)

                            dispatch(
                                Message.Load(
                                    isSuccess = true,
                                    roomList = roomInfos.data,
                                    indexSelectRoom = roomIndex
                                )
                            )
                            updateRoomInfo(roomInfos.data[roomIndex], getState().selectDate)
                        }

                        is Either.Error -> {
                            val save = roomInfos.error.saveData
                            dispatch(
                                Message.Load(
                                    isSuccess = false,
                                    roomList = save ?: listOf(RoomInfo.defaultValue),
                                    indexSelectRoom = 0
                                )
                            )
                        }
                    }
                }

                is Action.OnSettings -> dispatch(Message.OnSettings)
                Action.OnUpdateTimer -> dispatch(Message.UpdateTimer)
                Action.OnUpdateRoomInfo -> reboot(state = getState(), resetSelectRoom = false)
                Action.RefreshDate -> dispatch(Message.UpdateDate(GregorianCalendar())).apply {
                    updateDate(
                        GregorianCalendar()
                    )
                }
            }
        }

        private fun MainStore.State.indexRoom() =
            roomList.indexOfFirst { it.name == checkSettingsUseCase() }.run {
                if (this < 0) 0 else this
            }
    }

    private object ReducerImpl : Reducer<MainStore.State, Message> {
        @RequiresApi(Build.VERSION_CODES.N)
        override fun MainStore.State.reduce(message: Message): MainStore.State =
            when (message) {
                is Message.Load -> copy(
                    isLoad = false,
                    isData = message.isSuccess,
                    isError = !message.isSuccess,
                    roomList = if (message.isSuccess) message.roomList else roomList,
                    indexSelectRoom = message.indexSelectRoom,
                    timeToNextEvent = calcTimeToNextEvent()
                )

                is Message.UpdateDisconnect -> copy(
                    isDisconnect = message.newValue,
                    timeToNextEvent = calcTimeToNextEvent()
                )

                is Message.Reboot -> copy(
                    isError = false,
                    isLoad = true,
                    timeToNextEvent = calcTimeToNextEvent()
                )

                is Message.OnSettings -> copy(
                    isError = false,
                    isLoad = false,
                    isData = false,
                    isSettings = true
                )

                is Message.SelectRoom -> copy(
                    indexSelectRoom = message.index,
                    timeToNextEvent = calcTimeToNextEvent()
                )

                Message.UpdateTimer -> copy(timeToNextEvent = calcTimeToNextEvent())
                is Message.UpdateDate -> copy(selectDate = message.newDate)
                is Message.ShowToast -> copy(showToast = message.text)
            }

        private fun MainStore.State.calcTimeToNextEvent() =
            roomList.getOrNull(indexSelectRoom)?.currentEvent
                ?.run { ((finishTime.time.time - GregorianCalendar().time.time) / 60000).toInt() }
                ?: 0
    }
}