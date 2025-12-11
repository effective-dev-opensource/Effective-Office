package band.effective.office.tv.root

import band.effective.office.tv.autoplay.AutoplayComponent
import band.effective.office.tv.core.ui.model.ContentCategory
import band.effective.office.tv.feature.menu.presentation.MenuComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DelicateDecomposeApi
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

/**
 * Root navigation component for TV application.
 *
 * Navigation flow:
 * 1. Welcome → user clicks "Start Autoplay" → Autoplay with default categories (all)
 * 2. Welcome → user clicks "Settings" → Menu (choose categories) → Autoplay with selected categories
 * 3. Autoplay → back → Welcome
 *
 * Feature screens are managed internally by AutoplayComponent.
 */
class RootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    val childStack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        initialConfiguration = Config.Welcome,
        handleBackButton = true,
        serializer = kotlinx.serialization.serializer<Config>(),
        childFactory = ::createChild,
    )

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): Child = when (config) {
        is Config.Welcome -> Child.WelcomeChild(
            onStartAutoplay = { navigateToAutoplay(defaultCategories) },
            onOpenMenu = { navigateToMenu() }
        )
        is Config.Autoplay -> Child.AutoplayChild(
            component = AutoplayComponent(
                componentContext = componentContext,
                categories = config.categories,
                onBack = { navigateBack() }
            )
        )
        is Config.Menu -> Child.MenuChild(
            component = MenuComponent(
                componentContext = componentContext,
                initialCategories = defaultCategories,
                onBack = { navigateBack() },
                onStartAutoplay = { categories -> navigateToAutoplay(categories) }
            )
        )
    }

    @OptIn(DelicateDecomposeApi::class)
    private fun navigateToAutoplay(categories: Set<ContentCategory>) {
        navigation.push(Config.Autoplay(categories))
    }

    @OptIn(DelicateDecomposeApi::class)
    private fun navigateToMenu() {
        navigation.push(Config.Menu)
    }

    private fun navigateBack() {
        navigation.pop()
    }

    /**
     * Child screens for navigation.
     */
    sealed class Child {
        /**
         * Welcome screen - entry point of the app.
         */
        data class WelcomeChild(
            val onStartAutoplay: () -> Unit,
            val onOpenMenu: () -> Unit
        ) : Child()

        /**
         * Autoplay screen - slideshow container for feature screens.
         * Contains AutoplayComponent that manages the slideshow state and logic.
         */
        data class AutoplayChild(
            val component: AutoplayComponent
        ) : Child()

        /**
         * Menu screen - category selection for autoplay.
         * Contains MenuComponent that manages the menu state and logic.
         */
        data class MenuChild(
            val component: MenuComponent
        ) : Child()
    }

    @Serializable
    sealed class Config {
        @Serializable
        data object Welcome : Config()

        @Serializable
        data class Autoplay(val categories: Set<ContentCategory>) : Config()

        @Serializable
        data object Menu : Config()
    }

    companion object {
        /** Default categories when starting autoplay from Welcome screen */
        val defaultCategories = ContentCategory.entries.toSet()
    }
}

