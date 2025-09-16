package band.effective.office.tablet

import androidx.compose.runtime.Composable
import band.effective.office.tablet.core.ui.theme.AppTheme
import band.effective.office.tablet.root.Root
import band.effective.office.tablet.root.RootComponent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import band.effective.office.tablet.components.VersionOverlay

@Composable
fun App(rootComponent: RootComponent) {
    AppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Root(rootComponent)
            VersionOverlay()
        }
    }
}
