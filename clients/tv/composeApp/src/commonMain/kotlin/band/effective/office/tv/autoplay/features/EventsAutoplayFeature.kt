package band.effective.office.tv.autoplay.features

import androidx.compose.runtime.Composable
import band.effective.office.tv.autoplay.Direction
import band.effective.office.tv.autoplay.core.AutoplayFeature
import band.effective.office.tv.autoplay.core.NavigationHandler
import com.arkivanov.decompose.ComponentContext

/**
 * Placeholder implementation for Events autoplay feature.
 */
class EventsAutoplayFeature(
    componentContext: ComponentContext,
    onFinished: () -> Unit,
    onError: (String) -> Unit,
    setLoading: (Boolean) -> Unit,
) : AutoplayFeature {

    override val navigationHandler: NavigationHandler? = null

    @Composable
    override fun Content(isPlaying: Boolean) {
        // Placeholder content for Events autoplay
    }

    override fun onShown(direction: Direction) {
        // Handle when Events autoplay is shown
    }

    override fun onHidden() {
        // Handle when Events autoplay is hidden
    }
}