package band.effective.office.tv.feature.menu.presentation

import band.effective.office.tv.core.ui.model.ContentCategory

data class MenuState(
    val selectedCategories: List<ContentCategory>,
    val isLoading: Boolean,
    val error: String?,
) {
    /**
     * Check if at least one category is selected.
     */
    val canStartAutoplay: Boolean
        get() = selectedCategories.isNotEmpty()

    /**
     * Get selected categories as Set for compatibility.
     */
    val selectedCategoriesSet: Set<ContentCategory>
        get() = selectedCategories.toSet()

    companion object {
        /**
         * Default state with all categories selected in default order.
         */
        val defaultState = MenuState(
            selectedCategories = ContentCategory.entries,
            isLoading = false,
            error = null,
        )
    }
}
