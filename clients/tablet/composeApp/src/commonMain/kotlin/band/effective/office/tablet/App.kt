package band.effective.office.tablet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import band.effective.office.tablet.core.domain.manager.DateResetManager
import band.effective.office.tablet.core.domain.useCase.CheckSettingsUseCase
import band.effective.office.tablet.core.domain.useCase.ResourceDisposerUseCase
import band.effective.office.tablet.components.VersionOverlay
import band.effective.office.tablet.core.ui.inactivity.InactivityTracker
import band.effective.office.tablet.core.ui.inactivity.InactivityTracking
import band.effective.office.tablet.navigation.AppNavHost
import band.effective.office.tablet.time.TimeReceiver
import org.koin.compose.koinInject

@Composable
fun AppRoot() {
    val rootViewModelStoreOwner = rememberRootViewModelStoreOwner()

    CompositionLocalProvider(LocalViewModelStoreOwner provides rootViewModelStoreOwner) {
        // The theme is applied by the entry point, not here: its `Surface` has to sit above
        // AuroraWindowFrame so it paints the whole window, the strip under Aurora's status bar
        // included. Nothing about the Aurora window is handled here either — the rotation, the
        // inset and the dp space all live in that frame.
        InactivityTracker(modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                val resourceDisposerUseCase = koinInject<ResourceDisposerUseCase>()
                val checkSettingsUseCase = koinInject<CheckSettingsUseCase>()

                LaunchedEffect(Unit) { resourceDisposerUseCase() }
                // AppRoot is the one root shared by setContent, ComposeUIViewController and
                // the linux application {} — so the app-wide timers are started here.
                val timeReceiver = koinInject<TimeReceiver>()
                DisposableEffect(Unit) {
                    timeReceiver.start()
                    onDispose { timeReceiver.stop() }
                }
                LaunchedEffect(Unit) {
                    InactivityTracking.start()
                    InactivityTracking.timeouts.collect { DateResetManager.resetDate() }
                }

                val startRoomConfigured = remember { checkSettingsUseCase().isNotEmpty() }
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxSize()
                        .systemBarsPadding()
                ) {
                    AppNavHost(startRoomConfigured = startRoomConfigured)
                }
                VersionOverlay()
            }
        }
    }
}

@Composable
private fun rememberRootViewModelStoreOwner(): ViewModelStoreOwner = remember {
    object : ViewModelStoreOwner {
        override val viewModelStore: ViewModelStore = ViewModelStore()
    }
}
