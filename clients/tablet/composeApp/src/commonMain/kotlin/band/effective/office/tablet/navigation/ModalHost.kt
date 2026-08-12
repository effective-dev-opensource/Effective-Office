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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import band.effective.office.tablet.core.ui.platform.LocalModalHost
import band.effective.office.tablet.core.ui.platform.ModalHostState

/** What the dim is painted with, inherited from the pre-swap Decompose overlay. */
private const val DIM_ALPHA = 0.9f

/**
 * A centered modal on a full-screen dim, rendered in-composition. Tapping the dim dismisses; taps
 * on the content are absorbed.
 *
 * The modal-scoped [ViewModelStoreOwner] is about lifetime, not availability. Every platform
 * already offers a root one — the Activity on Android, the `ComposeUIViewController` on iOS, and on
 * Aurora the fork's own scene-scoped default (`findComposeDefaultViewModelStoreOwner`) — and all
 * three live as long as the app. A ViewModel is cached in its store by class and `parametersOf`
 * only runs when one is created, so under a root owner the booking editor opened for a second
 * booking would be handed the instance built for the first. Clearing the store on dispose is what
 * makes every open a fresh one. Keying the call per booking instead would leave an instance behind
 * for each, in a store that is never cleared, on a tablet that runs for weeks.
 *
 * This is the same scoping the modals had from their nested nav graph while they were `dialog<>`
 * destinations; it had to be re-created here when they became overlays.
 *
 * No rotation/density/inactivity wrappers here, unlike the `dialog<>` hosting this replaced: an
 * overlay is part of the main scene, so the ones `AppRoot` installs already cover it. The back
 * gesture, on the other hand, has to be caught here — see [ModalBackHandler]. What the on-screen
 * keyboard costs the card is worked out in [modalKeyboardShift]; this host only measures for it and
 * applies what it answers.
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

    val focusManager = LocalFocusManager.current
    val modal = remember { ModalHostState() }

    // Both boxes below report a height and a position, and those are not two ways of asking the
    // same thing. The heights feed composition — the shift is computed from them — so they have to
    // be values that invalidate it. The positions feed measurement in the layout phase, where the
    // field takes its bottom against the container and the overlay anchors against the card;
    // neither is ever read during composition. Nor can one stand in for the other: the same
    // `LayoutCoordinates` instance comes back every time, so a state holding it never reports a
    // change, and a height read off it would quietly stop updating exactly when it matters — when
    // the scene shortens for the keyboard on iOS.
    var containerHeight by remember { mutableStateOf(0) }
    var cardHeight by remember { mutableStateOf(0) }
    val keyboard = modalKeyboardShift(
        anchor = modal,
        containerHeightPx = containerHeight,
        cardHeightPx = cardHeight,
    )

    // A tap on the dim takes one step back, not two: with the keyboard up it puts the keyboard away
    // and leaves the modal, and only closes the modal once there is no keyboard to close. iOS has
    // no dismiss key on its keyboard, so without this the only way out of the keyboard is to close
    // the whole dialog.
    val onDimTap: () -> Unit = {
        if (modal.focusedFieldBottom != null) focusManager.clearFocus() else onDismiss()
    }

    CompositionLocalProvider(
        LocalViewModelStoreOwner provides storeOwner,
        LocalModalHost provides modal,
    ) {
        DimBox(
            onTap = onDimTap,
            onHeightMeasured = { containerHeight = it },
            onPositioned = { modal.containerCoords = it },
        ) {
            KeyboardShiftableBox(
                shift = keyboard,
                onHeightMeasured = { cardHeight = it },
                onPositioned = { modal.cardCoords = it },
            ) {
                content()
                // Inside the card's own box, so everything that moves the card moves the overlay
                // identically by construction — no transform arithmetic, no frame where the two
                // disagree. Drawn after the content so it sits on top; the box does not clip, so
                // content reaching above the card's top edge — a list opening upward — still shows.
                modal.overlay?.let { overlay -> overlay() }
            }
        }
    }
}

/**
 * The dim: fills the scene, paints it out and centers whatever is placed on it. [onTap] is the
 * whole of its behaviour — anything that should not dismiss has to absorb its own taps, which is
 * what [KeyboardShiftableBox] does.
 *
 * [onHeightMeasured] and [onPositioned] are both reported because both are needed and one cannot
 * be derived from the other — see the note in [ModalHost].
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
            .background(Color.Black.copy(alpha = DIM_ALPHA))
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
 * The card on the dim: drawn where [shift] says the keyboard leaves room for it, and swallowing the
 * taps that would otherwise reach the dim and dismiss the modal.
 *
 * The shift is applied in two ways, and neither is free to be the other. The height goes on as a
 * *required* maximum, so the card keeps being measured against the tallest the host has been rather
 * than against a scene iOS has shortened for the keyboard. The offset goes on as a draw-time
 * translation and never as a layout offset: the field inside reports its position from layout, so
 * moving the card there would feed the shift back into the number the shift is derived from, and
 * the card would jitter between two positions. See [ModalKeyboardShift] for where the two numbers
 * come from.
 *
 * [onHeightMeasured] gives the shift the card's height, which is what the downward half of it is
 * clamped against; [onPositioned] gives the overlay slot the node it anchors to. Same two channels
 * as [DimBox], for the same reason.
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
