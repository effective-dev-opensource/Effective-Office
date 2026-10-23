package band.effective.office.tv.feature.stories.presentation

import band.effective.office.shared.core.domain.Either
import band.effective.office.tv.core.ui.utils.componentCoroutineScope
import band.effective.office.tv.feature.stories.domain.service.StoriesDataProvider
import com.arkivanov.decompose.ComponentContext
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val STORY_DURATION_MS = 15_000L // 15 sec

/**
 * Decompose component for Stories feature.
 * Loads real data via StoriesDataProvider (Notion/Duolingo/Clockify/Supernova).
 * Manages auto-advance timer internally.
 */
class StoriesComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit,
    private val onError: (String) -> Unit = {},
    private val setLoading: (Boolean) -> Unit = {},
) : ComponentContext by componentContext, KoinComponent {

    private val dataProvider: StoriesDataProvider by inject()
    private val coroutineScope = componentCoroutineScope()

    private val mutableState = MutableStateFlow(StoriesState.initValue)
    val state: StateFlow<StoriesState> = mutableState.asStateFlow()

    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private var autoAdvanceJob: Job? = null

    private fun resetProgress() {
        _progress.value = 0f
    }

    private fun currentProgress(): Float = _progress.value.coerceIn(0f, 1f)

    init {
        loadStories()
    }

    /** Reset to the first story and restart progress bar. */
    fun restartFromStart() {
        stopAutoAdvance()
        mutableState.update { state ->
            if (state.items.isEmpty()) state else state.copy(currentIndex = 0)
        }
        resetProgress()
        val currentState = mutableState.value
        if (currentState.isPlaying && currentState.hasItems && !currentState.isLoading) {
            startAutoAdvance()
        }
    }

    /** Jump to the last story and start its timer from zero. */
    fun moveToLastFromStart() {
        stopAutoAdvance()
        mutableState.update { state ->
            if (state.items.isEmpty()) state else state.copy(currentIndex = state.items.lastIndex)
        }
        resetProgress()
        val currentState = mutableState.value
        if (currentState.isPlaying && currentState.hasItems && !currentState.isLoading) {
            startAutoAdvance()
        }
    }

    fun onIntent(intent: StoriesIntent) {
        when (intent) {
            StoriesIntent.Next -> moveNextOrFinish()
            StoriesIntent.Previous -> movePrevious()
            StoriesIntent.Retry -> loadStories()
            is StoriesIntent.SetPlaying -> setPlaying(intent.isPlaying)
        }
    }

    private fun loadStories() {
        setLoading(true)
        mutableState.update { StoriesState.loading() }

        coroutineScope.launch {
            dataProvider.loadStories().collect { result ->
                when (result) {
                    is Either.Success -> {
                        val storiesCount = result.data.stories.size
                        val warningsCount = result.data.warnings.size

                        Napier.d("Loaded $storiesCount stories${if (warningsCount > 0) ", $warningsCount warnings" else ""}")

                        mutableState.update { state ->
                            state.copy(
                                items = result.data.stories,
                                currentIndex = 0,
                                isLoading = false,
                                error = null,
                                warnings = result.data.warnings
                            )
                        }
                        resetProgress()
                        setLoading(false)

                        if (mutableState.value.isPlaying && mutableState.value.hasItems) {
                            startAutoAdvance()
                        } else {
                            stopAutoAdvance()
                        }
                    }
                    is Either.Error -> {
                        Napier.e("Load failed: ${result.error}")
                        mutableState.update { StoriesState.error(result.error) }
                        setLoading(false)
                        onError(result.error)
                    }
                }
            }
        }
    }

    private fun setPlaying(isPlaying: Boolean) {
        mutableState.update { it.copy(isPlaying = isPlaying) }
        val currentState = mutableState.value
        Napier.d("Playback ${if (isPlaying) "resumed" else "paused"}")
        // Only start auto-advance if playing AND we have loaded items
        if (isPlaying && currentState.hasItems && !currentState.isLoading) {
            startAutoAdvance()
        } else {
            stopAutoAdvance()
        }
    }

    private fun startAutoAdvance() {
        val currentState = mutableState.value
        if (currentState.items.isEmpty()) return

        val startProgress = currentProgress()
        val remainingDurationMs = ((1f - startProgress).coerceAtLeast(0f) * STORY_DURATION_MS).toLong()
        if (remainingDurationMs <= 0L) {
            moveNextOrFinish()
            return
        }

        autoAdvanceJob?.cancel()
        autoAdvanceJob = coroutineScope.launch {
            val startTime = System.currentTimeMillis()
            while (_progress.value < 1f) {
                delay(16) // ~60fps update
                val elapsed = System.currentTimeMillis() - startTime
                _progress.value = (startProgress + (elapsed.toFloat() / remainingDurationMs)).coerceAtMost(1f)
            }
            moveNextOrFinish()
        }
    }

    private fun stopAutoAdvance() {
        autoAdvanceJob?.cancel()
        autoAdvanceJob = null
    }

    private fun moveNextOrFinish() {
        val currentState = mutableState.value
        if (currentState.items.isEmpty()) {
            stopAutoAdvance()
            resetProgress()
            return
        }
        val nextIndex = currentState.currentIndex + 1
        if (nextIndex >= currentState.items.size) {
            if (currentState.items.size == 1) {
                // Single-story loop: stay on the only item and keep playing.
                resetProgress()
                if (currentState.isPlaying) {
                    coroutineScope.launch {
                        startAutoAdvance()
                    }
                } else stopAutoAdvance()
            } else {
                Napier.d("All stories shown - finishing")
                onFinished()
            }
        } else {
            Napier.d("Moving to next story (${nextIndex + 1}/${currentState.items.size})")
            mutableState.update { it.copy(currentIndex = nextIndex) }
            resetProgress()
            if (currentState.isPlaying) {
                startAutoAdvance()
            }
        }
    }

    private fun movePrevious() {
        val currentState = mutableState.value
        if (currentState.items.isEmpty()) return
        val prevIndex = if (currentState.currentIndex > 0) {
            currentState.currentIndex - 1
        } else {
            currentState.items.size - 1
        }
        mutableState.update { it.copy(currentIndex = prevIndex) }
        resetProgress()
        if (currentState.isPlaying) {
            startAutoAdvance()
        }
    }

    fun onHidden() {
        stopAutoAdvance()
    }
}
