package band.effective.office.tv.feature.photos.presentation

import band.effective.office.tv.feature.photos.domain.model.Photo

/**
 * UI state for the Photos slideshow feature.
 * Holds the list of photos, current position, loading/error status, and playback control.
 */
data class PhotosState(
    val items: List<Photo>,
    val currentIndex: Int,
    val isLoading: Boolean,
    val isPlaying: Boolean,
    val error: String?,
) {
    val hasItems: Boolean get() = items.isNotEmpty()
    /** Currently displayed photo, or null if list is empty or index out of bounds */
    val currentItem: Photo? get() = items.getOrNull(currentIndex)
    /** True if auto-advance can be started: has photos, not loading, no error */
    val canPlay: Boolean get() = hasItems && !isLoading

    companion object {
        val initial = PhotosState(
            items = emptyList(),
            currentIndex = 0,
            isLoading = true,
            isPlaying = false,
            error = null,
        )
    }
}
