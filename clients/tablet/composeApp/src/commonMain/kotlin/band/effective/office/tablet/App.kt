package band.effective.office.tablet

import androidx.compose.runtime.Composable
import band.effective.office.tablet.theme.AppTheme
import band.effective.office.tablet.root.Root
import band.effective.office.tablet.root.RootComponent
import com.arkivanov.decompose.ComponentContext

@Composable
fun App(rootComponent: RootComponent) {
    AppTheme { Root(rootComponent) }
}
