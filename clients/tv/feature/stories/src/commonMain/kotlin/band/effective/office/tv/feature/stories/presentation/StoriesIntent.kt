package band.effective.office.tv.feature.stories.presentation

/** Intents for Stories feature. */
sealed interface StoriesIntent {
    /** Move to next story card. */
    data object Next : StoriesIntent

    /** Move to previous story card. */
    data object Previous : StoriesIntent

    /** Retry loading stories after error. */
    data object Retry : StoriesIntent

    /** Set playing/paused state. */
    data class SetPlaying(val isPlaying: Boolean) : StoriesIntent
}


