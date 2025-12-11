package band.effective.office.tv.feature.menu.presentation

import band.effective.office.tv.core.ui.model.ContentCategory

data class MenuState(
    val selectedCategories: Set<ContentCategory>,
    val isLoading: Boolean,
    val error: String?,
) {
    /**
     * Check if at least one category is selected.
     */
    val canStartAutoplay: Boolean
        get() = selectedCategories.isNotEmpty()

    companion object {
        /**
         * Default state with all categories selected.
         */
        val defaultState = MenuState(
            selectedCategories = ContentCategory.entries.toSet(),
            isLoading = false,
            error = null,
        )
    }
}
