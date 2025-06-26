package band.effective.office.tablet.root

import band.effective.office.tablet.feature.main.MainComponent
import band.effective.office.tablet.feature.settings.SettingsComponent
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    val childStack: Value<ChildStack<*, Child>> = childStack(
        source = navigation,
        initialConfiguration = Config.Main,
        handleBackButton = true,
        serializer = kotlinx.serialization.serializer<Config>(),
        childFactory = ::createChild,
    )

    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): Child = when (config) {
        is Config.Main -> {
            Child.MainChild(MainComponent(componentContext = componentContext))
        }

        is Config.Settings -> {
            Child.SettingsChild(SettingsComponent(componentContext = componentContext))
        }
    }

    sealed class Child {
        data class MainChild(val component: MainComponent) : Child()
        data class SettingsChild(val component: SettingsComponent) : Child()
    }

    @Serializable
    sealed class Config {
        @Serializable
        object Settings : Config()

        @Serializable
        object Main : Config()
    }
}