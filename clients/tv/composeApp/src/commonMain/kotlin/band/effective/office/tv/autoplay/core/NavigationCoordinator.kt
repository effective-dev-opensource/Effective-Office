package band.effective.office.tv.autoplay.core

/**
 * Manages optional navigation handler from feature screens.
 *
 * Uses the handler instance as an owner token so that we don't accidentally
 * clear a newly set handler when the previous screen is disposed during
 * animated transitions.
 */
class NavigationCoordinator {
    private var navigationHandler: NavigationHandler? = null

    /** Register handler from the currently visible feature */
    fun setNavigationHandler(handler: NavigationHandler?) {
        navigationHandler = handler
    }

    fun clearNavigationHandler(expected: NavigationHandler?) {
        if (navigationHandler === expected) {
            navigationHandler = null
        }
    }

    /** Try to handle next-slide navigation inside current feature */
    fun handleNext(): Boolean = navigationHandler?.onNext() == true

    /** Try to handle previous-slide navigation inside current feature */
    fun handlePrev(): Boolean = navigationHandler?.onPrevious() == true
}

