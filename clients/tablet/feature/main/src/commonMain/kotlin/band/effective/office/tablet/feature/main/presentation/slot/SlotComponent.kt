package band.effective.office.tablet.feature.main.presentation.slot

import band.effective.office.tablet.core.domain.OfficeTime
import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.useCase.RoomInfoUseCase
import band.effective.office.tablet.core.domain.useCase.SlotUseCase
import band.effective.office.tablet.core.domain.useCase.TimerUseCase
import band.effective.office.tablet.core.domain.util.BootstrapperTimer
import band.effective.office.tablet.core.domain.util.asInstant
import band.effective.office.tablet.core.domain.util.currentInstant
import band.effective.office.tablet.core.domain.util.currentLocalDateTime
import band.effective.office.tablet.feature.main.domain.GetSlotsByRoomUseCase
import band.effective.office.tablet.feature.main.domain.mapper.SlotUiMapper
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
    openBookingDialog: (event: EventInfo, room: String) -> Unit,
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
    }

    fun sendIntent(intent: SlotIntent) {
        when (intent) {
            is SlotIntent.ClickOnSlot -> TODO()
            is SlotIntent.Delete -> TODO()
            is SlotIntent.OnCancelDelete -> TODO()
            is SlotIntent.UpdateDate -> updateDate(intent.newDate)
            is SlotIntent.UpdateRequest -> updateRequest(intent)
        }
    }

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

                if (uiSlots.isEmpty()) return@withContext

                val firstSlotStartInstant = uiSlots.first().slot.start.asInstant

                val delayDuration = (firstSlotStartInstant - currentInstant) + UPDATE_BEFORE_SLOT_START_MS

                updateTimer.restart(delayDuration)
            }
        }
    }
}