package band.effective.office.tv.feature.events.presentation

/**
 * User intents for the Events slideshow feature.
 */
sealed interface EventsIntent {
    /** Manually move to the next event */
    data object Next : EventsIntent

    /** Manually move to the previous event */
    data object Previous : EventsIntent

    /** Retry loading events after a failure */
    data object Retry : EventsIntent

    /** Enable or disable automatic slideshow playback */
    data class SetPlaying(val isPlaying: Boolean) : EventsIntent
}
