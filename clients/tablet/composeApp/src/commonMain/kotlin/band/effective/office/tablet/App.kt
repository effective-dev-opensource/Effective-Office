package band.effective.office.tablet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import band.effective.office.tablet.components.VersionOverlay
import band.effective.office.tablet.core.domain.useCase.CheckSettingsUseCase
import band.effective.office.tablet.core.domain.useCase.ResourceDisposerUseCase
import band.effective.office.tablet.core.ui.platform.ForcedLandscape
import band.effective.office.tablet.core.ui.theme.AppTheme
import band.effective.office.tablet.navigation.AppNavHost
import org.koin.compose.koinInject

@Composable
fun AppRoot() {
    val rootViewModelStoreOwner = rememberRootViewModelStoreOwner()

    CompositionLocalProvider(LocalViewModelStoreOwner provides rootViewModelStoreOwner) {
        AppTheme {
            ForcedLandscape {
                Box(modifier = Modifier.fillMaxSize()) {
                    val resourceDisposerUseCase = koinInject<ResourceDisposerUseCase>()
                    val checkSettingsUseCase = koinInject<CheckSettingsUseCase>()

                    LaunchedEffect(Unit) { resourceDisposerUseCase() }

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
}

@Composable
private fun rememberRootViewModelStoreOwner(): ViewModelStoreOwner = remember {
    object : ViewModelStoreOwner {
        override val viewModelStore: ViewModelStore = ViewModelStore()
    }
}
