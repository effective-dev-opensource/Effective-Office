package band.effective.office.tablet.feature.slot.presentation

import band.effective.office.tablet.core.domain.OfficeTime
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.Organizer
import band.effective.office.tablet.core.domain.model.Slot
import band.effective.office.tablet.core.domain.useCase.RoomInfoUseCase
import band.effective.office.tablet.core.domain.useCase.TimerUseCase
import band.effective.office.tablet.core.domain.util.BootstrapperTimer
import band.effective.office.tablet.core.domain.util.stableKey
import band.effective.office.shared.core.utils.asInstant
import band.effective.office.shared.core.utils.asLocalDateTime
import band.effective.office.shared.core.utils.currentInstant
import band.effective.office.tablet.feature.slot.domain.usecase.GetSlotsByRoomUseCase
import band.effective.office.tablet.feature.slot.presentation.mapper.SlotUiMapper
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private val SLOT_UPDATE_INTERVAL_MINUTES = 15.minutes
private val UPDATE_BEFORE_SLOT_START_MS = 60_000L.milliseconds

/** Presenter for a room's time slots. Runs on the [coroutineScope] its owner hands it. */
class SlotComponent(
    private val coroutineScope: CoroutineScope,
    val roomName: () -> String,
    private val openBookingDialog: (event: EventInfo, room: String) -> Unit,
    private val roomInfoUseCase: RoomInfoUseCase,
    private val timerUseCase: TimerUseCase,
    private val getSlotsByRoomUseCase: GetSlotsByRoomUseCase,
    private val slotUiMapper: SlotUiMapper,
) {

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
    }

    private fun resetAllMultiSlotStates() {
        val slots = state.value.slots.toMutableList()
        var hasChanges = false

        // Find all MultiSlot instances that are open and close them
        slots.forEachIndexed { index, slot ->
            if (slot is SlotUi.MultiSlot && slot.isOpen) {
                slots[index] = slot.copy(isOpen = false)
                hasChanges = true
            }
        }

        // Only update state if there were changes
        if (hasChanges) {
            mutableState.update { it.copy(slots = slots) }
        }
    }

    private suspend fun updateSlots(uiSlots: List<SlotUi>) = withContext(Dispatchers.Main.immediate) {
        if (uiSlots.isNotEmpty()) {
            val firstSlotStartInstant = uiSlots.first().slot.start.asInstant
            // The first slot may have started already, and a non-positive delay makes the timer spin.
            val delayDuration = ((firstSlotStartInstant - currentInstant) + UPDATE_BEFORE_SLOT_START_MS)
                .coerceAtLeast(UPDATE_BEFORE_SLOT_START_MS)

            updateTimer.restart(delayDuration)
        }
        // Publish even an empty list, or a day without free slots keeps the previous day's ones.
        mutableState.update { it.copy(slots = uiSlots.keepGroupsOpenedIn(it.slots)) }
    }

    private fun List<SlotUi>.keepGroupsOpenedIn(previous: List<SlotUi>): List<SlotUi> {
        val openedKeys = previous
            .filterIsInstance<SlotUi.MultiSlot>()
            .filter { it.isOpen }
            .mapTo(mutableSetOf()) { it.slot.stableKey() }

        if (openedKeys.isEmpty()) return this

        return map {
            if (it is SlotUi.MultiSlot && it.slot.stableKey() in openedKeys) {
                it.copy(isOpen = true)
            } else {
                it
            }
        }
    }

    fun sendIntent(intent: SlotIntent) {
        when (intent) {
            is SlotIntent.ClickToEdit -> handleClickToEdit(intent.slot)
            is SlotIntent.ClickToToggle -> openMultislot(intent.slot)
            is SlotIntent.UpdateRequest -> updateRequest(intent)
            SlotIntent.InactivityTimeout -> resetAllMultiSlotStates()
        }
    }

    private fun handleClickToEdit(slot: SlotUi) {
        when (slot) {
            is SlotUi.SimpleSlot -> slot.slot.execute()
            is SlotUi.NestedSlot -> slot.slot.execute()
            else -> {}
        }
    }

    private fun updateRequest(intent: SlotIntent.UpdateRequest) = coroutineScope.launch {
        roomInfoUseCase.getRoom(room = intent.room)?.let { roomInfo ->
            val slots = getSlotsByRoomUseCase(
                roomInfo = roomInfo,
                start = maxOf(
                    OfficeTime.startWorkTime(intent.newDate.date).asInstant,
                    currentInstant,
                ).asLocalDateTime
            )
            val uiSlots = slots.map(slotUiMapper::map)
            // Use updateSlots instead of directly updating state to preserve DeleteSlot and isOpen states
            updateSlots(uiSlots)
        }
    }

    private fun Slot.execute() = when (this) {
        is Slot.EmptySlot -> executeFreeSlot(this)
        is Slot.EventSlot -> executeEventSlot(this)
        else -> {}
    }

    private fun openMultislot(multislot: SlotUi.MultiSlot) {
        val key = multislot.slot.stableKey()
        mutableState.update { state ->
            state.copy(
                slots = state.slots.map {
                    if (it is SlotUi.MultiSlot && it.slot.stableKey() == key) {
                        it.copy(isOpen = !it.isOpen)
                    } else {
                        it
                    }
                },
            )
        }
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

/**
 * Assisted-injection factory for [SlotComponent]: Koin supplies the use cases, the owner supplies
 * the scope and the callbacks.
 */
fun interface SlotComponentFactory {
    fun create(
        coroutineScope: CoroutineScope,
        roomName: () -> String,
        openBookingDialog: (event: EventInfo, room: String) -> Unit,
    ): SlotComponent
}
