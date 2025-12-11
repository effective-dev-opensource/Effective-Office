package band.effective.office.tv.autoplay

import band.effective.office.tv.core.ui.model.ContentCategory

/**
 * State for autoplay slideshow
 */
data class AutoplayState(
    val screens: List<ContentCategory>,
    val currentIndex: Int,
    val isPlaying: Boolean,
    val direction: Direction,
    val isLoading: Boolean,
    val error: String?,
    val transitionKey: Int = 0,
) {
    val currentScreen: ContentCategory?
        get() = screens.getOrNull(currentIndex)

    companion object {
        fun initial(categories: Set<ContentCategory>): AutoplayState {
            val orderedScreens = listOf(
                ContentCategory.STORIES,
                ContentCategory.PHOTOS,
                ContentCategory.EVENTS
            ).filter { it in categories }

            return AutoplayState(
                screens = orderedScreens,
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
