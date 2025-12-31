package band.effective.office.tv.feature.stories.presentation.autoplay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import band.effective.office.tv.feature.stories.presentation.StoriesComponent
import band.effective.office.tv.feature.stories.presentation.StoriesIntent
import band.effective.office.tv.feature.stories.presentation.StoriesScreen
import com.arkivanov.decompose.ComponentContext
import band.effective.office.tv.core.ui.autoplay.Direction
import band.effective.office.tv.core.ui.autoplay.core.AutoplayFeature
import band.effective.office.tv.core.ui.autoplay.core.NavigationHandler

/**
 * Autoplay wrapper for the Stories feature.
 * Provides navigation handling and correct lifecycle callbacks when entering/leaving the autoplay carousel.
 */
class StoriesAutoplayFeature(
    componentContext: ComponentContext,
    onFinished: () -> Unit,
    onError: (String) -> Unit,
    setLoading: (Boolean) -> Unit,
) : AutoplayFeature {

    private val component = StoriesComponent(
        componentContext = componentContext,
        onFinished = onFinished,
        onError = onError,
        setLoading = setLoading
    )

    /** Allows autoplay carousel to consume arrow keys inside photo slideshow */
    override val navigationHandler: NavigationHandler = object : NavigationHandler {
        override fun onNext(): Boolean {
            component.onIntent(StoriesIntent.Next)
            return true
        }

        override fun onPrevious(): Boolean {
            val idx = component.state.value.currentIndex
            if (idx <= 0) return false
            component.onIntent(StoriesIntent.Previous)
            return true
        }
    }

    @Composable
    override fun Content(isPlaying: Boolean) {
        val stories = remember { component }
        StoriesScreen(
            component = stories,
            isPlaying = isPlaying
        )
    }

    /**
     * Called when this screen becomes visible in the autoplay carousel.
     * Restarts from beginning when coming from left, jumps to end when coming from right.
     */
    override fun onShown(direction: Direction) {
        when (direction) {
            Direction.FORWARD -> component.restartFromStart()
            Direction.BACKWARD -> component.moveToLastFromStart()
        }
    }

    override fun onHidden() {
        component.onHidden()
    }

    override fun retry() {
        component.onIntent(StoriesIntent.Retry)
    }
}
