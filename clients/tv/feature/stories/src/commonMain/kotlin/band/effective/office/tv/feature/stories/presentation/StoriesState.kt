package band.effective.office.tv.feature.stories.presentation

import band.effective.office.tv.feature.stories.domain.model.StoryDomainModel

/**
 * UI State for Stories feature.
 */
data class StoriesState(
    val items: List<StoryDomainModel>,
    val currentIndex: Int,
    val isLoading: Boolean,
    val isPlaying: Boolean,
    val error: String?,
    val warnings: List<String>,
) {
    /** Whether there are any stories to display. */
    val hasItems: Boolean get() = items.isNotEmpty()

    /** Currently displayed story, or null if no stories. */
    val currentItem: StoryDomainModel? get() = items.getOrNull(currentIndex)

    companion object {
        /** Initial/default state. */
        val initValue = StoriesState(
            items = emptyList(),
            currentIndex = 0,
            isLoading = true,
            isPlaying = false,
            error = null,
            warnings = emptyList()
        )

        /** Creates a loading state. */
        fun loading(): StoriesState = initValue.copy(isLoading = true, error = null, warnings = emptyList())

        /** Creates an error state. */
        fun error(message: String): StoriesState = initValue.copy(
            isLoading = false,
            error = message,
            warnings = emptyList()
        )
    }
}
