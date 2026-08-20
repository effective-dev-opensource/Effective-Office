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
import band.effective.office.tablet.components.VersionOverlay
import band.effective.office.tablet.core.domain.manager.DateResetManager
import band.effective.office.tablet.core.domain.useCase.CheckSettingsUseCase
import band.effective.office.tablet.core.domain.useCase.ResourceDisposerUseCase
import band.effective.office.tablet.core.ui.inactivity.InactivityTracker
import band.effective.office.tablet.core.ui.inactivity.InactivityTracking
import band.effective.office.tablet.core.ui.inactivity.LocalInactivityTracking
import band.effective.office.tablet.core.ui.theme.AppTheme
import band.effective.office.tablet.feature.main.domain.RefreshOnTimeZoneChangeUseCase
import band.effective.office.tablet.navigation.AppNavHost
import band.effective.office.tablet.time.TimeReceiver
import org.koin.compose.koinInject

@Composable
fun AppRoot() {
    val rootViewModelStoreOwner = rememberRootViewModelStoreOwner()
    val timeReceiver = koinInject<TimeReceiver>()
    DisposableEffect(Unit) {
        timeReceiver.start()
        onDispose { timeReceiver.stop() }
    }
    val resourceDisposerUseCase = koinInject<ResourceDisposerUseCase>()
    LaunchedEffect(Unit) { resourceDisposerUseCase() }
    val refreshOnTimeZoneChange = koinInject<RefreshOnTimeZoneChangeUseCase>()
    LaunchedEffect(Unit) { refreshOnTimeZoneChange() }
    val dateResetManager = koinInject<DateResetManager>()
    val inactivityTracking = koinInject<InactivityTracking>()
    DisposableEffect(inactivityTracking, dateResetManager) {
        val owner = inactivityTracking.start { dateResetManager.resetDate() }
        onDispose { inactivityTracking.stop(owner) }
    }
    val checkSettingsUseCase = koinInject<CheckSettingsUseCase>()
    val startRoomConfigured = remember { checkSettingsUseCase().isNotEmpty() }

    CompositionLocalProvider(
        LocalViewModelStoreOwner provides rootViewModelStoreOwner,
        LocalInactivityTracking provides inactivityTracking,
    ) {
        AppTheme {
            InactivityTracker(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .background(color = MaterialTheme.colorScheme.background)
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
}

@Composable
private fun rememberRootViewModelStoreOwner(): ViewModelStoreOwner = remember {
    object : ViewModelStoreOwner {
        override val viewModelStore: ViewModelStore = ViewModelStore()
    }
}
