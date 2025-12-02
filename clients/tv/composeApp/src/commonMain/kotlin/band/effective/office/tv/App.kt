package band.effective.office.tv

import androidx.compose.runtime.Composable
import band.effective.office.tv.theme.AppTheme
import band.effective.office.tv.root.Root
import band.effective.office.tv.root.RootComponent

/**
 * Main application entry point that renders the root component tree.
 */
@Composable
fun App(rootComponent: RootComponent) {
    AppTheme {
        Root(component = rootComponent)
    }
}


