package band.effective.office.tv.autoplay

/** User intents for the main autoplay screen */
sealed interface AutoplayIntent {
    data object NextScreen : AutoplayIntent         // Right arrow or auto-advance
    data object PreviousScreen : AutoplayIntent     // Left arrow
    data object TogglePause : AutoplayIntent        // Space or center button
    data object Retry : AutoplayIntent              // Retry after error
    data object Back : AutoplayIntent               // Escape or back button
}
