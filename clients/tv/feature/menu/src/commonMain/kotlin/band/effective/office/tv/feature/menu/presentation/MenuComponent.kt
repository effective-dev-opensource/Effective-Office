package band.effective.office.tv.feature.menu.presentation

import band.effective.office.tv.core.ui.model.ContentCategory
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Decompose component for menu screen.
 * Manages category selection state and navigation.
 */
class MenuComponent(
    componentContext: ComponentContext,
    initialCategories: List<ContentCategory> = ContentCategory.entries,
    private val onBack: () -> Unit,
    private val onStartAutoplay: (List<ContentCategory>) -> Unit,
) : ComponentContext by componentContext {

    private val mutableState = MutableStateFlow(
        MenuState.defaultState.copy(selectedCategories = initialCategories)
    )
    val state: StateFlow<MenuState> = mutableState.asStateFlow()

    /**
     * Handle user intents.
     */
    fun sendIntent(intent: MenuIntent) {
        when (intent) {
            is MenuIntent.ToggleCategory -> toggleCategory(intent.category)
            MenuIntent.StartAutoplay -> startAutoplay()
            MenuIntent.Back -> onBack()
        }
    }

    /**
     * Toggle category selection.
     */
    private fun toggleCategory(category: ContentCategory) {
        mutableState.update { currentState ->
            val newCategories = if (category in currentState.selectedCategories) {
                // Remove category, preserving order of others
                currentState.selectedCategories.filter { it != category }
            } else {
                // Add category to the end, preserving order
                currentState.selectedCategories + category
            }
            currentState.copy(selectedCategories = newCategories)
        }
    }

    /**
     * Start autoplay with selected categories.
     * Only proceeds if at least one category is selected.
     */
    private fun startAutoplay() {
        val currentState = state.value
        if (currentState.canStartAutoplay) {
            onStartAutoplay(currentState.selectedCategories)
        }
    }
}
