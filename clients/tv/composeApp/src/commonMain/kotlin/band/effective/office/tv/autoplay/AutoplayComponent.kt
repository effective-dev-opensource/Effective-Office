package band.effective.office.tv.autoplay

import band.effective.office.tv.autoplay.core.FeatureProvider
import band.effective.office.tv.autoplay.core.NavigationCoordinator
import band.effective.office.tv.core.ui.autoplay.AutoplayState
import band.effective.office.tv.core.ui.autoplay.Direction
import band.effective.office.tv.core.ui.autoplay.core.AutoplayFeature
import band.effective.office.tv.core.ui.autoplay.core.NavigationHandler
import band.effective.office.tv.core.ui.model.ContentCategory
import com.arkivanov.decompose.ComponentContext
import io.github.aakira.napier.Napier
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
    categories: List<ContentCategory>,
    private val onBack: () -> Unit,
) : ComponentContext by componentContext {

    private val featureProvider = FeatureProvider(
        componentContext = componentContext,
        onFinished = ::onScreenFinished,
        onError = ::onError,
        setLoading = ::setLoading,
    )
    private val navigationCoordinator = NavigationCoordinator()
    private val mutableState = MutableStateFlow(AutoplayState.initial(categories))
    val state: StateFlow<AutoplayState> = mutableState.asStateFlow()

    /**
     * Handle user intents
     */
    fun onIntent(intent: AutoplayIntent) {
        when (intent) {
            AutoplayIntent.NextScreen -> {
                if (!navigationCoordinator.handleNext()) nextScreen()
            }
            AutoplayIntent.PreviousScreen -> {
                if (!navigationCoordinator.handlePrev()) previousScreen()
            }
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
        Napier.d("Current screen finished - moving to next")
        if (state.value.isPlaying) {
            nextScreen()
        }
    }

    /**
     * Called by feature screens when an error occurs.
     * Shows error screen with retry option.
     */
    fun onError(message: String) {
        Napier.e("Screen error: $message")
        mutableState.update { it.copy(error = message, isPlaying = false) }
    }

    /**
     * Returns cached feature controller for a category, creating it on first access.
     */
    fun featureFor(category: ContentCategory?): AutoplayFeature? = featureProvider.featureFor(category)

    /** Clears cached features (e.g., when exiting autoplay). */
    fun clearFeaturesCache() = featureProvider.clearCache()

    /**
     * Allows feature screens to provide a navigation handler.
     * Handler should return true if it consumed navigation.
     */
    fun setNavigationHandler(handler: NavigationHandler?) {
        navigationCoordinator.setNavigationHandler(handler)
    }

    /** Clears navigation handler only if it is still the expected one. */
    fun clearNavigationHandler(handler: NavigationHandler?) {
        navigationCoordinator.clearNavigationHandler(handler)
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
        if (screens.isEmpty()) return

        val nextIndex = (state.value.currentIndex + 1) % screens.size
        val nextCategory = screens[nextIndex]

        Napier.d("Switching to next screen: $nextCategory (index $nextIndex/${screens.size})")

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
        if (screens.isEmpty()) return

        val prevIndex = (state.value.currentIndex + screens.size - 1) % screens.size
        val prevCategory = screens[prevIndex]

        Napier.d("Switching to previous screen: $prevCategory (index $prevIndex/${screens.size})")

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
        val newPlaying = !state.value.isPlaying
        Napier.d("Playback ${if (newPlaying) "resumed" else "paused"}")
        mutableState.update { it.copy(isPlaying = newPlaying) }
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
        val currentCategory = state.value.currentScreen
        currentCategory?.let { featureProvider.featureFor(it)?.retry() }
    }
}
