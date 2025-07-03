package band.effective.office.tablet.feature.main.presentation.slot

import band.effective.office.tablet.core.domain.OfficeTime
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.Organizer
import band.effective.office.tablet.core.domain.model.Slot
import band.effective.office.tablet.core.domain.useCase.RoomInfoUseCase
import band.effective.office.tablet.core.domain.useCase.SlotUseCase
import band.effective.office.tablet.core.domain.useCase.TimerUseCase
import band.effective.office.tablet.core.domain.util.BootstrapperTimer
import band.effective.office.tablet.core.domain.util.asInstant
import band.effective.office.tablet.core.domain.util.asLocalDateTime
import band.effective.office.tablet.core.domain.util.currentInstant
import band.effective.office.tablet.core.domain.util.currentLocalDateTime
import band.effective.office.tablet.feature.main.domain.GetSlotsByRoomUseCase
import band.effective.office.tablet.feature.main.presentation.mapper.SlotUiMapper
import com.arkivanov.decompose.ComponentContext
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val SLOT_UPDATE_INTERVAL_MINUTES = 15.minutes
private val UPDATE_BEFORE_SLOT_START_MS = 60_000L.milliseconds

class SlotComponent(
    private val componentContext: ComponentContext,
    val roomName: () -> String,
    private val openBookingDialog: (event: EventInfo, room: String) -> Unit,
) : ComponentContext by componentContext, KoinComponent {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val slotUseCase: SlotUseCase by inject()
    private val roomInfoUseCase: RoomInfoUseCase by inject()
    private val timerUseCase: TimerUseCase by inject()
    private val getSlotsByRoomUseCase: GetSlotsByRoomUseCase by inject()

    private val slotUiMapper: SlotUiMapper by inject()
    private val updateTimer = BootstrapperTimer(
        timerUseCase = timerUseCase,
        coroutineScope = coroutineScope,
    )

    private val mutableState = MutableStateFlow(State.initValue)
    val state = mutableState.asStateFlow()

    init {
        setupRoomAvailabilityWatcher()
        coroutineScope.launch {
            roomInfoUseCase.getRoom(roomName())?.let { roomInfo ->
                val uiSlots = getSlotsByRoomUseCase(roomInfo).map(slotUiMapper::map)
                updateSlots(uiSlots)
            }
        }
        coroutineScope.launch(Dispatchers.IO) {
            roomInfoUseCase.subscribe().collect { roomsInfo ->
                val roomInfo = roomsInfo.firstOrNull { it.name == roomName() } ?: return@collect
                val uiSlots = getSlotsByRoomUseCase(roomInfo).map(slotUiMapper::map)
                updateSlots(uiSlots)
            }
        }

    }

    private suspend fun updateSlots(uiSlots: List<SlotUi>) = withContext(Dispatchers.Main.immediate) {
        if (uiSlots.isNotEmpty()) {
            val firstSlotStartInstant = uiSlots.first().slot.start.asInstant
            val delayDuration = (firstSlotStartInstant - currentInstant) + UPDATE_BEFORE_SLOT_START_MS

            updateTimer.restart(delayDuration)
            mutableState.update { it.copy(slots = uiSlots) }
        }
    }

    fun sendIntent(intent: SlotIntent) {
        when (intent) {
            is SlotIntent.ClickOnSlot -> intent.slot.execute(state.value)
            is SlotIntent.Delete -> deleteSlot(intent)
            is SlotIntent.OnCancelDelete -> cancelDeletingSlot(intent)
            is SlotIntent.UpdateDate -> updateDate(intent.newDate)
            is SlotIntent.UpdateRequest -> updateRequest(intent)
        }
    }

    private fun cancelDeletingSlot(intent: SlotIntent.OnCancelDelete) {
        val slots = state.value.slots
        val original = intent.slot.original
        val newSlots = if (intent.slot.mainSlotIndex == null) {
            slots.toMutableList().apply { this[intent.slot.index] = original }
        } else {
            val mainSlot =
                (slots[intent.slot.mainSlotIndex] as SlotUi.MultiSlot).run {
                    copy(
                        subSlots = subSlots.toMutableList()
                            .apply { this[intent.slot.index] = original }
                    )
                }
            slots.toMutableList().apply { this[intent.slot.mainSlotIndex] = mainSlot }
        }
        mutableState.update { it.copy(slots = newSlots) }
    }

    private fun deleteSlot(intent: SlotIntent.Delete) {
        val slots = state.value.slots
        var mainSlot: SlotUi.MultiSlot? = null
        val uiSlot = slots.firstOrNull { it.slot == intent.slot }
            ?: slots.mapNotNull { (it as? SlotUi.MultiSlot)?.subSlots }.flatten()
                .firstOrNull { it.slot == intent.slot }
                ?.apply {
                    mainSlot = slots.mapNotNull { it as? SlotUi.MultiSlot }
                        .first { it.subSlots.contains(this) }
                }
        when {
            uiSlot == null -> {}
            mainSlot != null -> {
                val indexInMultiSlot = mainSlot.subSlots.indexOf(uiSlot)
                val indexMultiSlot = slots.indexOf(mainSlot)
                val newMainSlot = mainSlot.copy(
                    subSlots = mainSlot.subSlots.toMutableList().apply {
                        this[indexInMultiSlot] =
                            SlotUi.DeleteSlot(
                                slot = intent.slot,
                                onDelete = intent.onDelete,
                                original = uiSlot,
                                index = indexInMultiSlot,
                                mainSlotIndex = indexMultiSlot
                            )
                    })
                mutableState.update {
                    it.copy(slots = slots.toMutableList().apply {
                        this[indexMultiSlot] = newMainSlot
                    })
                }
            }

            else -> {
                val index = slots.indexOf(uiSlot)
                mutableState.update {
                    it.copy(
                        slots = slots.toMutableList().apply {
                            this[index] =
                                SlotUi.DeleteSlot(
                                    slot = intent.slot,
                                    onDelete = intent.onDelete,
                                    original = uiSlot,
                                    index = index,
                                    mainSlotIndex = null
                                )
                        }
                    )
                }
            }
        }
    }

    private fun SlotUi.execute(state: State) = when (this) {
        is SlotUi.DeleteSlot -> {}
        is SlotUi.MultiSlot -> openMultislot(this, state)
        is SlotUi.SimpleSlot -> slot.execute(state)
        is SlotUi.NestedSlot -> slot.execute(state)
        is SlotUi.LoadingSlot -> {
            slot.execute(state)
        }
    }

    private fun Slot.execute(state: State) = when (this) {
        is Slot.EmptySlot -> executeFreeSlot(this)
        is Slot.EventSlot -> executeEventSlot(this)
        is Slot.MultiEventSlot -> {}
        is Slot.LoadingEventSlot -> {}
    }

    private fun openMultislot(multislot: SlotUi.MultiSlot, state: State) {
        val slots = state.slots.toMutableList()
        val index = slots.indexOf(multislot)
        slots[index] = multislot.copy(isOpen = !multislot.isOpen)
        mutableState.update { it.copy(slots = slots) }
    }

    private fun executeFreeSlot(slot: Slot.EmptySlot) {
        openBookingDialog(slot.Event(), roomName())
    }

    private fun executeEventSlot(slot: Slot.EventSlot) {
        openBookingDialog(slot.eventInfo, roomName())
    }

    private fun Slot.EmptySlot.Event(): EventInfo =
        EventInfo(
            startTime = start,
            finishTime = finish.run {
                if (minuteDuration() <= 30) {
                    this
                } else {
                    start.asInstant.plus(30.minutes).asLocalDateTime
                }

            },
            organizer = Organizer.default,
            id = EventInfo.defaultId,
            isLoading = false,
        )

    private fun updateRequest(intent: SlotIntent.UpdateRequest) {
        if (!state.value.slots.any { it is SlotUi.DeleteSlot } || intent.refresh) {
            updateSlot(intent.room, intent.refresh)
        }
    }

    private fun updateSlot(roomName: String, refresh: Boolean) = coroutineScope.launch {
        if (refresh) {
            withContext(Dispatchers.IO) {
                roomInfoUseCase.updateCache()
            }
        }
        roomInfoUseCase.getRoom(roomName)?.let { roomInfo ->
            val slots = getSlotsByRoomUseCase(
                roomInfo = roomInfo,
            )
            val uiSlots = slots.map(slotUiMapper::map)
            mutableState.update { it.copy(slots = uiSlots) }
        }
    }

    private fun updateDate(newDate: LocalDateTime) = coroutineScope.launch {
        roomInfoUseCase.getRoom(room = roomName())?.let { roomInfo ->
            val slots = getSlotsByRoomUseCase(
                roomInfo = roomInfo,
                start = maxOf(
                    OfficeTime.startWorkTime(newDate),
                    currentLocalDateTime
                )
            )
            val uiSlots = slots.map(slotUiMapper::map)
            mutableState.update { it.copy(slots = uiSlots) }
        }
    }

    private fun setupRoomAvailabilityWatcher() {
        updateTimer.init(SLOT_UPDATE_INTERVAL_MINUTES) {
            withContext(Dispatchers.Main) {
                val roomInfo = roomInfoUseCase.getRoom(roomName()) ?: return@withContext

                val slots = getSlotsByRoomUseCase(roomInfo)
                val uiSlots = slots.map(slotUiMapper::map)

                updateSlots(uiSlots)
            }
        }
    }
}