package band.effective.office.tablet.feature.main

import com.arkivanov.decompose.ComponentContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus

@OptIn(ExperimentalTime::class)
class MainComponent(
    private val componentContext: ComponentContext,
    val onSettings: () -> Unit
) : ComponentContext by componentContext {

    private val componentScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(State.defaultState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _label = MutableSharedFlow<Label>()
    val label: SharedFlow<Label> = _label.asSharedFlow()

    init {
        // Initialize component
        componentScope.launch {
            // Check if settings are empty, if so, show settings screen
            if (shouldShowSettings()) {
                _state.value = _state.value.copy(
                    isSettings = true
                )
            }

            // Initial load of room information
            loadRoomInfo()
        }
    }

    private fun shouldShowSettings(): Boolean {
        // In the original code, this checked if checkSettingsUseCase().isEmpty()
        // For now, we'll return false to avoid showing settings screen
        return false
    }

    private fun loadRoomInfo() {
        componentScope.launch {
            // Set loading state
            _state.value = _state.value.copy(
                isLoad = true,
                isData = false,
                isError = false
            )

            try {
                // Simulate loading room info
                // In the original code, this would call roomInfoUseCase()
                withContext(Dispatchers.Default) {
                    // Simulate network delay
                    kotlinx.coroutines.delay(1000)
                }

                // Update state with loaded data
                _state.value = _state.value.copy(
                    isLoad = false,
                    isData = true,
                    roomList = listOf(
                        "Room 1",
                        "Room 2",
                        "Room 3"
                    ),
                    selectedDate = Clock.System.now(),
                )
            } catch (e: Exception) {
                // Handle error
                _state.value = _state.value.copy(
                    isLoad = false,
                    isError = true
                )

                // Show error toast
                showToast("Error loading room information: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        componentScope.launch {
            _label.emit(Label.ShowToast(message))
        }
    }

    private fun reboot(refresh: Boolean = false) {
        componentScope.launch {
            if (refresh) {
                _state.value = _state.value.copy(
                    isLoad = true,
                    isData = false
                )
            }

            // Reload room info
            loadRoomInfo()
        }
    }

    private fun updateRoomInfo(roomIndex: Int) {
        // In the original code, this would update the selected room info
        // For now, we'll just update the selected room index
        _state.value = _state.value.copy(
            indexSelectRoom = roomIndex
        )
    }

    private fun updateDate(updateInDays: Int) {
        // In the original code, this would update the selected date
        // For now, we'll just update the selected date string
        val newDate = if (updateInDays > 0) {
            "Today + $updateInDays days"
        } else if (updateInDays < 0) {
            "Today - ${-updateInDays} days"
        } else {
            "Today"
        }

        _state.value = _state.value.copy(
            selectedDate = Clock.System.now().plus(1, DateTimeUnit.HOUR),
        )
    }

    // Intent handling
    fun sendIntent(intent: Intent) {
        when (intent) {
            is Intent.OnOpenFreeRoomModal -> {
                // In the original code, this would open a modal window
                // For now, we'll just show a toast
                showToast("Opening free room modal")
            }

            is Intent.RebootRequest -> {
                reboot(refresh = true)
            }

            is Intent.OnChangeEventRequest -> {
                // In the original code, this would open a modal window to change an event
                // For now, we'll just show a toast
                showToast("Opening change event modal")
            }

            is Intent.OnSelectRoom -> {
                updateRoomInfo(intent.index)
            }

            Intent.OnUpdate -> {
                reboot(refresh = false)
            }

            is Intent.OnFastBooking -> {
                // In the original code, this would open a modal window for fast booking
                // For now, we'll just show a toast
                showToast("Opening fast booking modal for ${intent.minDuration} minutes")
            }

            is Intent.OnUpdateSelectDate -> {
                updateDate(intent.updateInDays)
            }

            Intent.OnResetSelectDate -> {
                updateDate(0)
            }
        }
    }
}
