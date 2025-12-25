package band.effective.office.tv.feature.events.presentation

import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.collectToEitherList
import band.effective.office.shared.core.utils.componentCoroutineScope
import band.effective.office.tv.feature.events.domain.model.EventInfo
import band.effective.office.tv.feature.events.domain.repository.EventsRepository
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import io.github.aakira.napier.Napier

const val EVENT_DURATION_MS = 15_000L // 15 sec

/**
 * Decompose component responsible for managing the events slideshow.
 * Notifies parent via [onFinished] when slideshow ends.
 */
class EventsComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit,
    private val onError: (String) -> Unit = {},
    private val setLoading: (Boolean) -> Unit = {},
) : ComponentContext by componentContext, KoinComponent {

    private val repository: EventsRepository by inject()
    private val scope = componentCoroutineScope()

    private val _state = MutableStateFlow(EventsState.initial)
    val state: StateFlow<EventsState> = _state.asStateFlow()

    private var autoAdvanceJob: Job? = null
    private var loadJob: Job? = null

    init {
        loadEvents()
    }

    /** Restart slideshow from the first event */
    fun restartFromStart() {
        if (!state.value.hasItems) return
        _state.update { it.copy(currentIndex = 0) }
        Napier.v("Index updated to 0")
        val current = state.value
        if (current.isPlaying && !current.isLoading) startAutoAdvance()
    }

    /** Jump directly to the last event */
    fun moveToLastFromStart() {
        val items = state.value.items
        if (items.isEmpty()) return
        _state.update { it.copy(currentIndex = items.lastIndex) }
        Napier.v("Index updated to ${items.lastIndex}")
        val current = state.value
        if (current.isPlaying && !current.isLoading) startAutoAdvance()
    }

    fun onHidden() {
        setPlaying(false)
        stopAutoAdvance()
    }

    fun onIntent(intent: EventsIntent) {
        when (intent) {
            EventsIntent.Next -> moveNextOrFinish()
            EventsIntent.Previous -> movePrevious()
            EventsIntent.Retry -> loadEvents()
            is EventsIntent.SetPlaying -> setPlaying(intent.isPlaying)
        }
    }

    private fun loadEvents() {
        loadJob?.cancel()
        loadJob = scope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            setLoading(true)

            when (val result = repository.getEvents().collectToEitherList()) {
                is Either.Success -> handleLoadSuccess(result.data)
                is Either.Error -> handleLoadError(result.error.description)
            }
        }
    }

    private fun handleLoadSuccess(events: List<EventInfo>) {
        Napier.d("Loaded ${events.size} events")

        _state.update {
            it.copy(
                items = events,
                currentIndex = 0,
                isLoading = false,
                error = null
            )
        }
        setLoading(false)

        if (events.isEmpty()) {
            Napier.d("No events, finishing")
            onFinished()
            return
        }

        val current = state.value
        if (current.isPlaying) {
            startAutoAdvance()
        } else {
            stopAutoAdvance()
        }
    }

    private fun handleLoadError(message: String) {
        Napier.e("Load error - $message")

        _state.update {
            it.copy(
                isLoading = false,
                error = message
            )
        }
        setLoading(false)
        stopAutoAdvance()
        onError(message)
    }

    /**
     * Enable or disable auto-advance.
     * When enabled and events are available, starts timer for next event.
     */
    private fun setPlaying(isPlaying: Boolean) {
        _state.update { it.copy(isPlaying = isPlaying) }
        Napier.d("Playing: $isPlaying")
        val current = state.value
        if (isPlaying && current.hasItems && !current.isLoading) {
            startAutoAdvance()
        } else {
            stopAutoAdvance()
        }
    }

    /** Schedule transition to the next event after delay */
    private fun startAutoAdvance() {
        val current = state.value
        if (current.items.isEmpty()) return

        autoAdvanceJob?.cancel()
        Napier.d("Auto-advance started, index=${current.currentIndex}")
        autoAdvanceJob = scope.launch {
            delay(EVENT_DURATION_MS)
            moveNextOrFinish()
        }
    }

    private fun stopAutoAdvance() {
        autoAdvanceJob?.cancel()
        autoAdvanceJob = null
        Napier.d("Auto-advance stopped")
    }

    // Navigation
    private fun moveNextOrFinish() {
        val current = state.value
        if (current.items.isEmpty()) {
            stopAutoAdvance()
            return
        }

        val nextIndex = current.currentIndex + 1
        if (current.items.size == 1) {
            if (current.isPlaying) startAutoAdvance() else stopAutoAdvance()
            return
        }

        if (nextIndex >= current.items.size) {
            Napier.d("Events: Reached end, finishing")
            stopAutoAdvance()
            onFinished()
        } else {
            _state.update { it.copy(currentIndex = nextIndex) }
            Napier.v("Index updated to $nextIndex")
            if (current.isPlaying) startAutoAdvance()
        }
    }

    private fun movePrevious() {
        val current = state.value
        if (current.items.isEmpty()) return
        val prevIndex = (current.currentIndex - 1).coerceAtLeast(0)
        _state.update { it.copy(currentIndex = prevIndex) }
        Napier.v("Index updated to $prevIndex")
        if (current.isPlaying) startAutoAdvance()
    }
}
