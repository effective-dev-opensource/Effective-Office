package band.effective.office.tv.core.ui.autoplay.core

import androidx.compose.runtime.Composable
import band.effective.office.tv.core.ui.autoplay.Direction

/**
 * Autoplay feature controller that knows how to render its content
 * and optionally intercept navigation (next/previous slide).
 */
interface AutoplayFeature {
    val navigationHandler: NavigationHandler?

    @Composable
    fun Content(isPlaying: Boolean)


    /** Triggered when the autoplay error screen wants to retry the current feature. */
    fun retry() {}

    /**
     * Called when screen becomes visible in autoplay carousel.
     * Direction is the last slideshow move (FORWARD/BACKWARD).
     */
    fun onShown(direction: Direction) {}

    /** Called when screen leaves autoplay carousel. */
    fun onHidden() {}
}

