package band.effective.office.tv.feature.events.presentation.autoplay

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import band.effective.office.tv.feature.events.presentation.EventsComponent
import band.effective.office.tv.feature.events.presentation.EventsIntent
import band.effective.office.tv.feature.events.presentation.EventsScreen
import com.arkivanov.decompose.ComponentContext
import band.effective.office.tv.core.ui.autoplay.Direction
import band.effective.office.tv.core.ui.autoplay.core.AutoplayFeature
import band.effective.office.tv.core.ui.autoplay.core.NavigationHandler

/**
 * Autoplay wrapper for the Events feature.
 * Provides navigation handling and correct lifecycle callbacks when entering/leaving the autoplay carousel.
 */
class EventsAutoplayFeature(
    componentContext: ComponentContext,
    onFinished: () -> Unit,
    onError: (String) -> Unit,
    setLoading: (Boolean) -> Unit,
) : AutoplayFeature {

    private val component = EventsComponent(
        componentContext = componentContext,
        onFinished = onFinished,
        onError = onError,
        setLoading = setLoading
    )

    /** Allows autoplay carousel to consume arrow keys inside events slideshow */
    override val navigationHandler: NavigationHandler = object : NavigationHandler {
        override fun onNext(): Boolean {
            component.onIntent(EventsIntent.Next)
            return true
        }

        override fun onPrevious(): Boolean {
            val idx = component.state.value.currentIndex
            if (idx <= 0) return false
            component.onIntent(EventsIntent.Previous)
            return true
        }
    }

    @Composable
    override fun Content(isPlaying: Boolean) {
        val events = remember { component }
        EventsScreen(
            component = events,
            isPlaying = isPlaying
        )
    }

    /** Called when this screen becomes visible in the autoplay carousel. */
    override fun onShown(direction: Direction) {
        when (direction) {
            Direction.FORWARD -> component.restartFromStart()
            Direction.BACKWARD -> component.moveToLastFromStart()
        }
    }

    override fun onHidden() {
        component.onHidden()
    }
}
