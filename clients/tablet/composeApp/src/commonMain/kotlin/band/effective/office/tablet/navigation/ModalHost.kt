package band.effective.office.tablet.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import band.effective.office.tablet.core.ui.platform.LocalModalHost
import band.effective.office.tablet.core.ui.platform.ModalHostState
import band.effective.office.tablet.platform.ModalBackHandler
import band.effective.office.tablet.platform.modalKeyboardPadding

/** Matches the dim the pre-swap overlays used. */
private const val MODAL_SCRIM_ALPHA = 0.9f

/**
 * Dimmed backdrop with a centered card: tapping the dim dismisses, taps on the card are absorbed,
 * and the back gesture is taken by [ModalBackHandler]. Owns a modal-scoped [ViewModelStoreOwner]
 * cleared on dispose, so the next modal starts on a fresh ViewModel.
 */
@Composable
internal fun ModalHost(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit,
) {
    ModalBackHandler(onBack = onDismiss)

    val storeOwner = remember {
        object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore = ViewModelStore()
        }
    }
    DisposableEffect(Unit) {
        onDispose { storeOwner.viewModelStore.clear() }
    }

    val modal = remember { ModalHostState() }

    CompositionLocalProvider(
        LocalViewModelStoreOwner provides storeOwner,
        LocalModalHost provides modal,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = MODAL_SCRIM_ALPHA))
                .modalKeyboardPadding()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .onGloballyPositioned { modal.cardCoords = it }
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {},
                    ),
            ) {
                content()
                // Drawn after the content and never clipped, so a list opening upward still shows.
                modal.overlay?.invoke()
            }
        }
    }
}
