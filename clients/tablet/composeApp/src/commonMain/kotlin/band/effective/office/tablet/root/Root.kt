package band.effective.office.tablet.root

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import band.effective.office.tablet.feature.main.presentation.main.MainScreen
import band.effective.office.tablet.feature.settings.SettingsScreen
import com.arkivanov.decompose.extensions.compose.stack.Children

@Composable
fun Root(component: RootComponent) {
    Box(
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxSize()
            .systemBarsPadding()
    ) {
        Children(
            stack = component.childStack,
        ) { child ->
            when (val instance = child.instance) {
                is RootComponent.Child.MainChild -> MainScreen(instance.component)
                is RootComponent.Child.SettingsChild -> SettingsScreen(instance.component)
            }
        }
    }
}