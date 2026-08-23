package band.effective.office.tablet.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import band.effective.office.tablet.core.ui.platform.LocalModalHost
import band.effective.office.tablet.core.ui.platform.ModalHostState
import band.effective.office.tablet.platform.ModalBackHandler

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

    // Both boxes report a height and a position, and those are not two ways of asking the same
    // thing. The heights feed composition, so they have to be values that invalidate it; the
    // positions feed measurement and are never read while composing. Neither stands in for the
    // other: the same LayoutCoordinates instance comes back every time, so a state holding one
    // never reports a change, and a height read off it would stop updating exactly when it matters.
    var containerHeight by remember { mutableStateOf(0) }
    var cardHeight by remember { mutableStateOf(0) }
    val keyboard = modalKeyboardShift(
        anchor = modal,
        containerHeightPx = containerHeight,
        cardHeightPx = cardHeight,
    )

    CompositionLocalProvider(
        LocalViewModelStoreOwner provides storeOwner,
        LocalModalHost provides modal,
    ) {
        DimBox(
            onTap = onDismiss,
            onHeightMeasured = { containerHeight = it },
            onPositioned = { modal.containerCoords = it },
        ) {
            KeyboardShiftableBox(
                shift = keyboard,
                onHeightMeasured = { cardHeight = it },
                onPositioned = { modal.cardCoords = it },
            ) {
                content()
                // Drawn after the content and never clipped, so a list opening upward still shows.
                modal.overlay?.invoke()
            }
        }
    }
}

/**
 * The dim: fills the scene, paints it out and centers what is placed on it. [onTap] is the whole of
 * its behaviour — anything that should not dismiss absorbs its own taps.
 */
@Composable
private fun DimBox(
    onTap: () -> Unit,
    onHeightMeasured: (Int) -> Unit,
    onPositioned: (LayoutCoordinates) -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { onHeightMeasured(it.height) }
            .onGloballyPositioned(onPositioned)
            .background(Color.Black.copy(alpha = MODAL_SCRIM_ALPHA))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onTap,
            ),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

/**
 * The card on the dim, drawn where [shift] says the keyboard leaves room for it. The cap is a
 * *required* one so the card keeps its full height, and the offset is a draw-time translation and
 * never a layout offset — the field inside reports its position from layout.
 */
@Composable
private fun KeyboardShiftableBox(
    shift: ModalKeyboardShift,
    onHeightMeasured: (Int) -> Unit,
    onPositioned: (LayoutCoordinates) -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .requiredHeightIn(max = shift.maxCardHeight)
            .graphicsLayer { translationY = -shift.offsetPx.toFloat() }
            .onSizeChanged { onHeightMeasured(it.height) }
            .onGloballyPositioned(onPositioned)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {},
            ),
        content = content,
    )
}
