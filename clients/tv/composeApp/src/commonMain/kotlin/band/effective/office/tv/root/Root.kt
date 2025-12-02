package band.effective.office.tv.root

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import band.effective.office.tv.WelcomeScreen
import com.arkivanov.decompose.extensions.compose.stack.Children

/**
 * Root composable for Decompose navigation.
 * Basic navigation setup with childStack.
 */
@Composable
fun Root(component: RootComponent, modifier: Modifier = Modifier) {
    Children(
        stack = component.childStack,
        modifier = modifier,
    ) { child ->
        when (child.instance) {
            is RootComponent.Child.WelcomeChild -> WelcomeScreen()
        }
    }
}

