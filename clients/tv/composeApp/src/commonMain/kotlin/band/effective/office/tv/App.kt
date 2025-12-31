package band.effective.office.tv

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import band.effective.office.tv.core.ui.theme.AppTheme
import band.effective.office.tv.core.ui.theme.LocalTvColorsPalette
import band.effective.office.tv.root.Root
import band.effective.office.tv.root.RootComponent

/**
 * Main application entry point that renders the root component tree.
 */
@Composable
fun App(rootComponent: RootComponent) {
    AppTheme {
        val colors = LocalTvColorsPalette.current
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background),
            color = colors.background
        ) {
            Root(component = rootComponent)
        }
    }
}


