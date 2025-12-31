package band.effective.office.tv.feature.events.presentation

import band.effective.office.tv.feature.events.domain.model.EventInfo

/**
 * UI state for the Events slideshow feature.
 * Holds the list of events, current position, loading/error status, and playback control.
 */
data class EventsState(
    val items: List<EventInfo>,
    val currentIndex: Int,
    val isLoading: Boolean,
    val isPlaying: Boolean,
    val error: String?,
) {
    val hasItems: Boolean get() = items.isNotEmpty()
    /** Currently displayed event, or null if list is empty or index out of bounds */
    val currentItem: EventInfo? get() = items.getOrNull(currentIndex)
    /** True if auto-advance can be started: has events, not loading, no error */
    val canPlay: Boolean get() = hasItems && !isLoading && error == null

    companion object {
        val initial = EventsState(
            items = emptyList(),
            currentIndex = 0,
            isLoading = true,
            isPlaying = false,
            error = null
        )
    }
}
