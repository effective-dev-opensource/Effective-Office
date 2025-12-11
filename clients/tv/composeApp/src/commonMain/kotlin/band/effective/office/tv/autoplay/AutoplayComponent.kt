package band.effective.office.tv.autoplay

import band.effective.office.tv.core.ui.model.ContentCategory
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Component for managing autoplay slideshow of feature screens.
 *
 * Controls cycling through selected content categories
 * with automatic advancement after a delay.
 */
class AutoplayComponent(
    componentContext: ComponentContext,
    categories: Set<ContentCategory>,
    private val onBack: () -> Unit,
) : ComponentContext by componentContext {

    private val mutableState = MutableStateFlow(AutoplayState.initial(categories))
    val state: StateFlow<AutoplayState> = mutableState.asStateFlow()

    /**
     * Handle user intents
     */
    fun onIntent(intent: AutoplayIntent) {
        when (intent) {
            AutoplayIntent.NextScreen -> nextScreen()
            AutoplayIntent.PreviousScreen -> previousScreen()
            AutoplayIntent.TogglePause -> togglePause()
            AutoplayIntent.Retry -> retry()
            AutoplayIntent.Back -> onBack()
        }
    }

    /**
     * Called by feature screens when they finish showing all their content.
     * This triggers transition to the next screen.
     */
    fun onScreenFinished() {
        if (state.value.isPlaying) {
            nextScreen()
        }
    }

    /**
     * Called by feature screens when an error occurs.
     * Shows error screen with retry option.
     */
    fun onError(message: String) {
        mutableState.update {
            it.copy(
                error = message,
                isPlaying = false
            )
        }
    }

    /**
     * Called by feature screens when loading starts.
     */
    fun setLoading(isLoading: Boolean) {
        mutableState.update { it.copy(isLoading = isLoading) }
    }

    /**
     * Switch to next screen in the slideshow.
     *
     * Increment transitionKey on every transition to force AnimatedContent to animate,
     * even if target states look equal (e.g., looping between indexes).
     */
    private fun nextScreen() {
        val screens = state.value.screens
        if (screens.size <= 1) return

        val nextIndex = (state.value.currentIndex + 1) % screens.size
        mutableState.update {
            it.copy(
                currentIndex = nextIndex,
                direction = Direction.FORWARD,
                transitionKey = it.transitionKey + 1
            )
        }
    }

    /**
     * Switch to previous screen in the slideshow.
     */
    private fun previousScreen() {
        val screens = state.value.screens
        if (screens.size <= 1) return

        val prevIndex = (state.value.currentIndex + screens.size - 1) % screens.size
        mutableState.update {
            it.copy(
                currentIndex = prevIndex,
                direction = Direction.BACKWARD,
                transitionKey = it.transitionKey + 1
            )
        }
    }

    /**
     * Toggle pause/play state
     */
    private fun togglePause() {
        mutableState.update { it.copy(isPlaying = !it.isPlaying) }
    }

    /**
     * Retry current screen from scratch.
     */
    private fun retry() {
        mutableState.update {
            it.copy(
                isPlaying = true,
                error = null,
                isLoading = false,
                transitionKey = it.transitionKey + 1
            )
        }
    }
}
