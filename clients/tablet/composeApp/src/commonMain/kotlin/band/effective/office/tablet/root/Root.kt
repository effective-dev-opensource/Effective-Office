package band.effective.office.tablet.root

import androidx.compose.runtime.Composable
import band.effective.office.tablet.feature.main.presentation.main.MainScreen
import band.effective.office.tablet.feature.settings.SettingsScreen
import com.arkivanov.decompose.extensions.compose.stack.Children

@Composable
fun Root(component: RootComponent) {
    Children(
        stack = component.childStack
    ) { child ->
        when (val instance = child.instance) {
            is RootComponent.Child.MainChild -> MainScreen(instance.component)
            is RootComponent.Child.SettingsChild -> SettingsScreen(instance.component)
        }
    }
}