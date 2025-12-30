package band.effective.office.tv.core.ui.autoplay.core

/**
 * Delegate for handling in-screen navigation (slides) before switching autoplay screens.
 * Used by features to consume navigation events when they have internal slideshow control.
 */
interface NavigationHandler {
    /** @return true if handled next-slide navigation */
    fun onNext(): Boolean

    /** @return true if handled previous-slide navigation */
    fun onPrevious(): Boolean
}

