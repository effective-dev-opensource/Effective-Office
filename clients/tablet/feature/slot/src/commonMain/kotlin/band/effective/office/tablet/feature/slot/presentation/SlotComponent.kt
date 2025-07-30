package band.effective.office.tablet.feature.slot.presentation

import band.effective.office.tablet.core.domain.OfficeTime
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.Organizer
import band.effective.office.tablet.core.domain.model.Slot
import band.effective.office.tablet.core.domain.useCase.RoomInfoUseCase
import band.effective.office.tablet.core.domain.useCase.TimerUseCase
import band.effective.office.tablet.core.domain.util.BootstrapperTimer
import band.effective.office.tablet.core.domain.util.asInstant
import band.effective.office.tablet.core.domain.util.asLocalDateTime
import band.effective.office.tablet.core.domain.util.currentInstant
import band.effective.office.tablet.feature.slot.domain.usecase.GetSlotsByRoomUseCase
import band.effective.office.tablet.feature.slot.presentation.mapper.SlotUiMapper
import com.arkivanov.decompose.ComponentContext
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
    }

    private suspend fun updateSlots(uiSlots: List<SlotUi>) = withContext(Dispatchers.Main.immediate) {
        if (uiSlots.isNotEmpty()) {
            val firstSlotStartInstant = uiSlots.first().slot.start.asInstant
            val delayDuration = (firstSlotStartInstant - currentInstant) + UPDATE_BEFORE_SLOT_START_MS

            updateTimer.restart(delayDuration)

            // Get all DeleteSlot instances, including those inside MultiSlot objects
            val deletingSlots = getAllDeleteSlots(state.value.slots)

            // Get current MultiSlots that are open
            val openMultiSlots = state.value.slots.filterIsInstance<SlotUi.MultiSlot>()
                .filter { it.isOpen }

            // If there are no deleting slots and no open MultiSlots, just update with new slots
            if (deletingSlots.isEmpty() && openMultiSlots.isEmpty()) {
                mutableState.update { it.copy(slots = uiSlots) }

                return@withContext
            }

            // Merge new slots with existing "deleting" slots and preserve open MultiSlots
            val mergedSlots = uiSlots.map { newSlot ->
                // Find if this slot matches any slot that was being deleted
                val matchingDeleteSlot = deletingSlots.find { it.slot.isSameSlot(newSlot.slot) }

                if (matchingDeleteSlot != null) {
                    // Preserve the DeleteSlot state but update with new slot data
                    SlotUi.DeleteSlot(
                        slot = newSlot.slot,
                        onDelete = matchingDeleteSlot.onDelete,
                        original = newSlot,
                        index = uiSlots.indexOf(newSlot),
                        mainSlotIndex = null, // Will be updated for nested slots
                        startTimeMillis = matchingDeleteSlot.startTimeMillis,
                        deletionProgress = matchingDeleteSlot.deletionProgress
                    )
                } else if (newSlot is SlotUi.MultiSlot) {
                    // Check if this MultiSlot was open before
                    val wasOpen = openMultiSlots.any { it.slot.isSameSlot(newSlot.slot) }

                    // Handle MultiSlot case - check if any subslots were being deleted
                    val updatedSubSlots = newSlot.subSlots.map { subSlot ->
                        val matchingSubDeleteSlot = deletingSlots.find { it.slot.isSameSlot(subSlot.slot) }
                        Napier.d(
                            tag = "DebugDeleting",
                            message = "matchingSubDeleteSlot: $matchingSubDeleteSlot, subSlot: $subSlot, newSlot.subSlots: $newSlot.subSlots"
                        )
                        if (matchingSubDeleteSlot != null) {
                            SlotUi.DeleteSlot(
                                slot = subSlot.slot,
                                onDelete = matchingSubDeleteSlot.onDelete,
                                original = subSlot,
                                index = newSlot.subSlots.indexOf(subSlot),
                                mainSlotIndex = uiSlots.indexOf(newSlot),
                                startTimeMillis = matchingSubDeleteSlot.startTimeMillis,
                                deletionProgress = matchingSubDeleteSlot.deletionProgress
                            )
                        } else {
                            subSlot
                        }
                    }

                    // Preserve the isOpen state
                    newSlot.copy(
                        subSlots = updatedSubSlots,
                        isOpen = wasOpen
                    )
                } else {
                    newSlot
                }
            }

            mutableState.update { it.copy(slots = mergedSlots) }
        }
    }

    fun sendIntent(intent: SlotIntent) {
        when (intent) {
            is SlotIntent.ClickToEdit -> handleClickToEdit(intent.slot)
            is SlotIntent.ClickToToggle -> openMultislot(intent.slot)
            is SlotIntent.Delete -> deleteSlot(intent)
            is SlotIntent.OnCancelDelete -> cancelDeletingSlot(intent)
            is SlotIntent.UpdateRequest -> updateRequest(intent)
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

    private fun cancelDeletingSlot(intent: SlotIntent.OnCancelDelete) {
        val slots = state.value.slots
        val original = intent.slot.original
        val newSlots = if (intent.slot.mainSlotIndex == null) {
            slots.toMutableList().apply { this[intent.slot.index] = original }
        } else {
            val mainSlot =
                (slots[intent.slot.mainSlotIndex as Int] as SlotUi.MultiSlot).run {
                    copy(
                        subSlots = subSlots.toMutableList()
                            .apply { this[intent.slot.index] = original }
                    )
                }
            slots.toMutableList().apply { this[intent.slot.mainSlotIndex as Int] = mainSlot }
        }
        mutableState.update { it.copy(slots = newSlots) }
    }

    private fun deleteSlot(intent: SlotIntent.Delete) {
        val slots = state.value.slots
        var mainSlot: SlotUi.MultiSlot? = null
        val uiSlot = slots.firstOrNull { it.slot.isSameSlot(intent.slot) }
            ?: slots.mapNotNull { (it as? SlotUi.MultiSlot)?.subSlots }.flatten()
                .firstOrNull { it.slot.isSameSlot(intent.slot) }
                ?.apply {
                    mainSlot = slots.mapNotNull { it as? SlotUi.MultiSlot }
                        .first { multiSlot -> multiSlot.subSlots.any { subSlot -> subSlot.slot.isSameSlot(this.slot) } }
                }
        when {
            uiSlot == null -> {}
            mainSlot != null -> {
                val indexInMultiSlot = mainSlot!!.subSlots.indexOfFirst { it.slot.isSameSlot(uiSlot.slot) }
                val indexMultiSlot = slots.indexOfFirst { it === mainSlot }
                val newMainSlot = mainSlot!!.copy(
                    subSlots = mainSlot!!.subSlots.toMutableList().apply {
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
                val index = slots.indexOfFirst { it.slot.isSameSlot(uiSlot.slot) }
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

    private fun Slot.execute() = when (this) {
        is Slot.EmptySlot -> executeFreeSlot(this)
        is Slot.EventSlot -> executeEventSlot(this)
        else -> {}
    }

    private fun openMultislot(multislot: SlotUi.MultiSlot) {
        val slots = state.value.slots.toMutableList()
        val index = slots.indexOfFirst { it.slot.isSameSlot(multislot.slot) }
        if (index < 0) return
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

    // Add this extension function to check if two slots represent the same slot
    private fun Slot.isSameSlot(other: Slot): Boolean {
        // For EventSlots, compare by event ID
        if (this is Slot.EventSlot && other is Slot.EventSlot) {
            return this.eventInfo.id == other.eventInfo.id
        }

        // For other slots, compare by time range
        return this.start == other.start && this.finish == other.finish
    }

    // Helper function to get all DeleteSlot instances, including those inside MultiSlot objects
    private fun getAllDeleteSlots(slots: List<SlotUi>): List<SlotUi.DeleteSlot> {
        val result = mutableListOf<SlotUi.DeleteSlot>()

        // Add top-level DeleteSlot instances
        result.addAll(slots.filterIsInstance<SlotUi.DeleteSlot>())

        // Add DeleteSlot instances inside MultiSlot objects
        slots.filterIsInstance<SlotUi.MultiSlot>().forEach { multiSlot ->
            result.addAll(multiSlot.subSlots.filterIsInstance<SlotUi.DeleteSlot>())
        }

        return result
    }
}
