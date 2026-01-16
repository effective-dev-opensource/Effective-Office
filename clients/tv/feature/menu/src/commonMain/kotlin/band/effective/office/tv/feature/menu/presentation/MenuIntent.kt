package band.effective.office.tv.feature.menu.presentation

import band.effective.office.tv.core.ui.model.ContentCategory

sealed interface MenuIntent {
    /** Toggle category selection on/off. */
    data class ToggleCategory(val category: ContentCategory) : MenuIntent

    /** Start autoplay with currently selected categories. */
    data object StartAutoplay : MenuIntent

    /** Navigate back to previous screen. */
    data object Back : MenuIntent

    data object OpenUpdate: MenuIntent
}
