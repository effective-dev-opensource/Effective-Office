package band.effective.office.tv.root

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import band.effective.office.tv.autoplay.AutoplayScreen
import band.effective.office.tv.core.ui.screen.WelcomeScreen
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.feature.menu.presentation.MenuScreen
import band.effective.office.tv.platform.SelfUpdateScreen
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation

/**
 * Root composable for Decompose navigation.
 * Handles navigation between Welcome, Autoplay, and Menu screens.
 */
@Composable
fun Root(component: RootComponent, modifier: Modifier = Modifier) {
    val colors = LocalTvColorsPalette.current

    Box(
        modifier = modifier
            .background(color = colors.background)
            .fillMaxSize()
    ) {
        Children(
            stack = component.childStack,
            animation = stackAnimation(fade()),
        ) { child ->
            when (val instance = child.instance) {
                is RootComponent.Child.WelcomeChild -> WelcomeScreen(
                    onStartAutoplay = instance.onStartAutoplay,
                    onOpenMenu = instance.onOpenMenu
                )

                is RootComponent.Child.AutoplayChild -> AutoplayScreen(
                    component = instance.component
                )

                is RootComponent.Child.MenuChild -> MenuScreen(
                    component = instance.component
                )

                is RootComponent.Child.SelfUpdateChild -> SelfUpdateScreen(
                    componentContext = instance.component
                )
            }
        }
    }
}

