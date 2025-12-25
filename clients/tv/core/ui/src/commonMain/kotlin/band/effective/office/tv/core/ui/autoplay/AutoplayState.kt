package band.effective.office.tv.core.ui.autoplay

import band.effective.office.tv.core.ui.model.ContentCategory

/**
 * UI state for the autoplay carousel.
 * Contains ordered list of active screens and current position.
 */
data class AutoplayState(
    val screens: List<ContentCategory>,
    val currentIndex: Int,
    val isPlaying: Boolean,
    val direction: Direction,
    val isLoading: Boolean,
    val error: String?,
    /** Incremented on every transition to force AnimatedContent animation even when looping */
    val transitionKey: Int = 0,
) {
    val currentScreen: ContentCategory?
        get() = screens.getOrNull(currentIndex)

    companion object {
        /**
         * Builds initial state with categories in the order they were selected.
         * Order is preserved from menu selection.
         */
        fun initial(categories: List<ContentCategory>): AutoplayState {
            return AutoplayState(
                screens = categories,
                currentIndex = 0,
                isPlaying = true,
                direction = Direction.FORWARD,
                isLoading = false,
                error = null,
                transitionKey = 0,
            )
        }
    }
}

/**
 * Direction of screen transition
 */
enum class Direction {
    FORWARD,
    BACKWARD
}
