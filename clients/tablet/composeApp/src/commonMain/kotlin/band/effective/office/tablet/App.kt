package band.effective.office.tablet

import androidx.compose.runtime.Composable
import band.effective.office.tablet.core.ui.theme.AppTheme
import band.effective.office.tablet.root.Root
import band.effective.office.tablet.root.RootComponent

@Composable
fun App(rootComponent: RootComponent) {
    AppTheme { Root(rootComponent) }
}
