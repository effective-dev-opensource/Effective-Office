package band.effective.office.tv.autoplay

/**
 * User intents for autoplay screen
 */
sealed interface AutoplayIntent {
    data object NextScreen : AutoplayIntent
    data object PreviousScreen : AutoplayIntent
    data object TogglePause : AutoplayIntent
    data object Retry : AutoplayIntent
    data object Back : AutoplayIntent
}
