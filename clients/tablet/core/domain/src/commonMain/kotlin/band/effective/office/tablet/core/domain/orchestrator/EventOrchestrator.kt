package band.effective.office.tablet.core.domain.orchestrator

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlinx.datetime.Clock

/**
 * Orchestrates various refresh events in the application, allowing different
 * refresh triggers to have their own specific idle time configurations.
 */
class EventOrchestrator(
    private val coroutineScope: CoroutineScope
) {
    // Tracks if user is actively interacting with the UI
    private val _userInteractionState = MutableStateFlow(UserInteractionState.IDLE)
    val userInteractionState = _userInteractionState.asStateFlow()

    // Collects pending refresh requests by trigger type
    private val _pendingRefreshes = MutableStateFlow<Map<RefreshTrigger, PendingRefresh>>(emptyMap())

    // Emits coordinated refresh events
    private val _refreshEvents = MutableSharedFlow<RefreshEvent>()
    val refreshEvents = _refreshEvents.asSharedFlow()

    // Tracks when user started interacting
    private var interactionStartTime: Long = 0
    private var interactionTimeoutJob: Job? = null

    // Inactivity timer job
    private var inactivityJob: Job? = null

    // Configuration for each trigger type
    private val triggerConfigs = mutableMapOf<RefreshTrigger, TriggerConfig>()

    init {
        // Set default configurations for each trigger type
        setDefaultTriggerConfigs()

        // Monitor user interaction state changes
        coroutineScope.launch {
            _userInteractionState.collect { state ->
                when (state) {
                    UserInteractionState.IDLE -> processPendingRefreshes()
                    UserInteractionState.ACTIVE -> { /* User is active, do nothing */ }
                    UserInteractionState.INACTIVE -> { /* Handled by inactivity timer */ }
                }
            }
        }
    }

    /**
     * Sets default configurations for each trigger type
     */
    private fun setDefaultTriggerConfigs() {
        // Firebase events should refresh as soon as user stops interacting
        triggerConfigs[RefreshTrigger.FIREBASE_EVENT] = TriggerConfig(
            idleTime = 500.milliseconds,
            resetToCurrentDate = false,
            resetToDefaultRoom = false,
            priority = 1 // High priority
        )

        // Meeting start/end events should refresh quickly
        triggerConfigs[RefreshTrigger.MEETING_START] = TriggerConfig(
            idleTime = 1.seconds,
            resetToCurrentDate = false,
            resetToDefaultRoom = false,
            priority = 2
        )

        triggerConfigs[RefreshTrigger.MEETING_END] = TriggerConfig(
            idleTime = 1.seconds,
            resetToCurrentDate = false,
            resetToDefaultRoom = false,
            priority = 2
        )

        // Inactivity timeout should reset date and room
        triggerConfigs[RefreshTrigger.INACTIVITY_TIMEOUT] = TriggerConfig(
            idleTime = 1.minutes,
            resetToCurrentDate = true,
            resetToDefaultRoom = true,
            priority = 3
        )

        // Manual refresh should happen quickly
        triggerConfigs[RefreshTrigger.MANUAL_REFRESH] = TriggerConfig(
            idleTime = 100.milliseconds,
            resetToCurrentDate = false,
            resetToDefaultRoom = false,
            priority = 0 // Highest priority
        )
    }

    /**
     * Configures a specific trigger type with custom settings
     */
    fun configureTrigger(
        trigger: RefreshTrigger,
        idleTime: Duration,
        resetToCurrentDate: Boolean = false,
        resetToDefaultRoom: Boolean = false,
        priority: Int = 5
    ) {
        triggerConfigs[trigger] = TriggerConfig(
            idleTime = idleTime,
            resetToCurrentDate = resetToCurrentDate,
            resetToDefaultRoom = resetToDefaultRoom,
            priority = priority
        )
    }

    /**
     * Called when user starts interacting with UI
     */
    fun startUserInteraction() {
        interactionTimeoutJob?.cancel()
        inactivityJob?.cancel()
        interactionStartTime = Clock.System.now().toEpochMilliseconds()
        _userInteractionState.value = UserInteractionState.ACTIVE
    }

    /**
     * Called when user completes an interaction
     */
    fun endUserInteraction() {
        interactionTimeoutJob?.cancel()
        interactionTimeoutJob = coroutineScope.launch {
            delay(100) // Small buffer before changing to IDLE
            _userInteractionState.value = UserInteractionState.IDLE
        }

        // Start inactivity timer
        startInactivityTimer()
    }

    /**
     * Starts the inactivity timer
     */
    private fun startInactivityTimer() {
        val inactivityConfig = triggerConfigs[RefreshTrigger.INACTIVITY_TIMEOUT] 
            ?: TriggerConfig(1.minutes, true, true, 3)

        inactivityJob?.cancel()
        inactivityJob = coroutineScope.launch {
            delay(inactivityConfig.idleTime)
            _userInteractionState.value = UserInteractionState.INACTIVE

            // Emit inactivity event
            _refreshEvents.emit(RefreshEvent(
                triggers = setOf(RefreshTrigger.INACTIVITY_TIMEOUT),
                resetToCurrentDate = inactivityConfig.resetToCurrentDate,
                resetToDefaultRoom = inactivityConfig.resetToDefaultRoom
            ))
        }
    }

    /**
     * Request a refresh from any source
     */
    fun requestRefresh(trigger: RefreshTrigger, force: Boolean = false) {
        val config = triggerConfigs[trigger] ?: TriggerConfig(
            idleTime = 1.seconds,
            resetToCurrentDate = false,
            resetToDefaultRoom = false,
            priority = 5
        )

        coroutineScope.launch {
            if (force || _userInteractionState.value == UserInteractionState.IDLE) {
                // If forcing or user is idle, emit immediately
                emitRefreshEvent(setOf(trigger))
            } else {
                // Otherwise queue the refresh with its specific idle time
                val pendingRefresh = PendingRefresh(
                    trigger = trigger,
                    timestamp = Clock.System.now().toEpochMilliseconds(),
                    config = config
                )

                _pendingRefreshes.update { current ->
                    current + (trigger to pendingRefresh)
                }

                // Start a timer for this specific trigger if user is active
                if (_userInteractionState.value == UserInteractionState.ACTIVE) {
                    startTriggerSpecificTimer(pendingRefresh)
                }
            }
        }
    }

    /**
     * Starts a timer for a specific trigger
     */
    private fun startTriggerSpecificTimer(pendingRefresh: PendingRefresh) {
        coroutineScope.launch {
            delay(pendingRefresh.config.idleTime)

            // Only process if still in pending state and user is active
            if (_pendingRefreshes.value.containsKey(pendingRefresh.trigger) && 
                _userInteractionState.value == UserInteractionState.ACTIVE) {

                // Process this specific trigger
                emitRefreshEvent(setOf(pendingRefresh.trigger))

                // Remove from pending
                _pendingRefreshes.update { current ->
                    current - pendingRefresh.trigger
                }
            }
        }
    }

    /**
     * Process all pending refreshes
     */
    private suspend fun processPendingRefreshes() {
        val pendingTriggers = _pendingRefreshes.value

        if (pendingTriggers.isNotEmpty()) {
            // Group by priority to process highest priority first
            val groupedByPriority = pendingTriggers.values.groupBy { it.config.priority }
            val highestPriority = groupedByPriority.keys.minOrNull()

            highestPriority?.let { priority ->
                val highestPriorityTriggers = groupedByPriority[priority]?.map { it.trigger }?.toSet() ?: emptySet()

                if (highestPriorityTriggers.isNotEmpty()) {
                    emitRefreshEvent(highestPriorityTriggers)

                    // Remove processed triggers
                    _pendingRefreshes.update { current ->
                        current.filterKeys { it !in highestPriorityTriggers }
                    }
                }
            }
        }
    }

    /**
     * Emits a refresh event with the given triggers
     */
    private suspend fun emitRefreshEvent(triggers: Set<RefreshTrigger>) {
        // Determine if we need to reset date or room based on trigger configs
        val resetToCurrentDate = triggers.any { 
            triggerConfigs[it]?.resetToCurrentDate ?: false 
        }

        val resetToDefaultRoom = triggers.any { 
            triggerConfigs[it]?.resetToDefaultRoom ?: false 
        }

        _refreshEvents.emit(RefreshEvent(
            triggers = triggers,
            resetToCurrentDate = resetToCurrentDate,
            resetToDefaultRoom = resetToDefaultRoom
        ))
    }

    /**
     * User interaction states
     */
    enum class UserInteractionState {
        IDLE,      // User not interacting
        ACTIVE,    // User actively interacting
        INACTIVE   // User has been inactive for a while
    }

    /**
     * Types of refresh triggers
     */
    enum class RefreshTrigger {
        FIREBASE_EVENT,
        MEETING_START,
        MEETING_END,
        INACTIVITY_TIMEOUT,
        MANUAL_REFRESH
    }

    /**
     * Configuration for a specific trigger type
     */
    data class TriggerConfig(
        val idleTime: Duration,
        val resetToCurrentDate: Boolean,
        val resetToDefaultRoom: Boolean,
        val priority: Int // Lower number = higher priority
    )

    /**
     * Represents a pending refresh request
     */
    data class PendingRefresh(
        val trigger: RefreshTrigger,
        val timestamp: Long,
        val config: TriggerConfig
    )

    /**
     * Event emitted when a refresh should occur
     */
    data class RefreshEvent(
        val triggers: Set<RefreshTrigger>,
        val resetToCurrentDate: Boolean = false,
        val resetToDefaultRoom: Boolean = false
    )
}
