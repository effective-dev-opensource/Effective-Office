package band.effective.office.tablet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import band.effective.office.tablet.core.ui.theme.AppTheme
import band.effective.office.tablet.root.Root
import band.effective.office.tablet.root.RootComponent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import band.effective.office.tablet.components.VersionOverlay
import band.effective.office.tablet.feature.main.domain.RefreshOnTimeZoneChangeUseCase
import band.effective.office.tablet.time.TimeReceiver
import org.koin.compose.koinInject

@Composable
fun App(rootComponent: RootComponent) {
    val timeReceiver = koinInject<TimeReceiver>()
    DisposableEffect(Unit) {
        timeReceiver.start()
        onDispose { timeReceiver.stop() }
    }
    val refreshOnTimeZoneChange = koinInject<RefreshOnTimeZoneChangeUseCase>()
    LaunchedEffect(Unit) { refreshOnTimeZoneChange() }
    AppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Root(rootComponent)
            VersionOverlay()
        }
    }
}
