package band.effective.office.tv.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

/**
 * Root navigation component for TV application.
 *
 * Basic Decompose navigation setup with single Welcome screen.
 * Navigation will be extended with feature screens in later stages.
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
        is Config.Welcome -> Child.WelcomeChild
    }

    sealed class Child {
        object WelcomeChild : Child()
    }

    @Serializable
    sealed class Config {
        @Serializable
        object Welcome : Config()
    }
}

