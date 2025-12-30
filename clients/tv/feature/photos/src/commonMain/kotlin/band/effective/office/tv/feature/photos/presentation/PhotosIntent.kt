package band.effective.office.tv.feature.photos.presentation

/**
 * User intents for the Photos slideshow feature.
 */
sealed interface PhotosIntent {
    /** Manually move to the next photo */
    data object Next : PhotosIntent

    /** Manually move to the previous photo */
    data object Previous : PhotosIntent

    /** Retry loading photos after a failure */
    data object Retry : PhotosIntent

    /** Enable or disable automatic slideshow playback */
    data class SetPlaying(val isPlaying: Boolean) : PhotosIntent

    /** Remove a specific photo that failed to load (triggered from UI on image error) */
    data class RemoveFailedPhoto(val errorUrl: String) : PhotosIntent
}
