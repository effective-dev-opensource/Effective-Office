package band.effective.office.tablet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import band.effective.office.tablet.core.ui.theme.AppTheme
import band.effective.office.tablet.root.Root
import band.effective.office.tablet.root.RootComponent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import band.effective.office.tablet.components.VersionOverlay
import band.effective.office.tablet.core.domain.manager.DateResetManager
import band.effective.office.tablet.core.ui.inactivity.InactivityTracker
import band.effective.office.tablet.core.ui.inactivity.InactivityTracking
import band.effective.office.tablet.core.ui.inactivity.LocalInactivityTracking
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
    val dateResetManager = koinInject<DateResetManager>()
    val inactivityTracking = koinInject<InactivityTracking>()
    DisposableEffect(inactivityTracking, dateResetManager) {
        val owner = inactivityTracking.start { dateResetManager.resetDate() }
        onDispose { inactivityTracking.stop(owner) }
    }
    CompositionLocalProvider(LocalInactivityTracking provides inactivityTracking) {
        AppTheme {
            InactivityTracker(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Root(rootComponent)
                    VersionOverlay()
                }
            }
        }
    }
}
