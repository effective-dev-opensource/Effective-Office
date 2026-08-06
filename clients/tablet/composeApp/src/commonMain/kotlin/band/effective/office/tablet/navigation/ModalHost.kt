package band.effective.office.tablet.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import band.effective.office.tablet.core.ui.platform.LocalFocusedFieldBottom
import band.effective.office.tablet.core.ui.platform.softKeyboardOverlapPx
import io.github.aakira.napier.Napier
import kotlin.math.roundToInt

/** How much air to leave between the field being typed into and the top of the keyboard. */
private val FIELD_TO_KEYBOARD_GAP = 8.dp

/** Same log tag family as `SoftKeyboard` and `OrganizerPicker`, so one grep reads the whole story. */
private const val MODAL_TAG = "ModalHost"

/**
 * Full-screen dim (0.9 black, matching the pre-swap Decompose overlay) behind a centered modal,
 * rendered in-composition. Provides a modal-scoped [ViewModelStoreOwner] that is cleared when the
 * modal leaves the composition, so each modal gets a fresh ViewModel. Tapping the dim dismisses;
 * taps on the content are absorbed.
 *
 * No rotation/density/inactivity wrappers here, unlike the `dialog<>` hosting this replaced: an
 * overlay is part of the main scene, so the ones `AppRoot` installs already cover it. The back
 * gesture, on the other hand, has to be caught here — see [ModalBackHandler].
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

    CompositionLocalProvider(LocalViewModelStoreOwner provides storeOwner) {
        // Move the card out from under the keyboard, rather than padding the box it sits in:
        // padding shrinks that box, and a card taller than what is left gets squeezed and clipped
        // from the inside instead of by the screen edge.
        //
        // Keeping the card at full height is the other half of the same problem. On iOS the system
        // shortens the scene when the keyboard opens, and the card would be measured against the
        // shorter box and shrink — the squeezing. It cannot simply be measured unbounded either:
        // the card scrolls inside, and Compose refuses an infinite height above a scrolling
        // container. So it is measured against the tallest the box has ever been, which is the
        // screen before the keyboard took its share.
        var fullHeight by remember { mutableStateOf(0) }
        val density = LocalDensity.current

        // What has to clear the keyboard is the focused field, not the card around it: aim for its
        // bottom edge sitting [FIELD_TO_KEYBOARD_GAP] above the keyboard, and move the card by
        // however much that costs. Nothing focused, or the field already high enough — no shift.
        val focusedFieldBottom = remember { mutableStateOf<Int?>(null) }
        val overlapPx = softKeyboardOverlapPx()
        val gapPx = with(density) { FIELD_TO_KEYBOARD_GAP.roundToPx() }
        // Measured rather than taken from the window size: on iOS `LocalWindowInfo.containerSize`
        // comes back with the sides swapped — 1668 as the height of a 1668x2420 portrait screen —
        // and everything derived from it lands nowhere near the keyboard.
        var containerBottom by remember { mutableStateOf(0) }
        var containerHeight by remember { mutableStateOf(0) }
        var cardHeight by remember { mutableStateOf(0) }

        // Where the field sits with nothing shifted, captured once while the shift is still zero.
        // Deriving it from the live position instead would feed the shift back into its own input:
        // move the card, the field moves with it, the number shrinks, the shift shrinks — and the
        // card jitters between two positions. The keyboard is still animating at this point, so the
        // shift keeps recomputing against a moving keyboardTop, which is fine — that end is real.
        var restingFieldBottom by remember { mutableStateOf<Int?>(null) }
        val measuredFieldBottom = focusedFieldBottom.value
        if (measuredFieldBottom == null) {
            restingFieldBottom = null
        } else if (restingFieldBottom == null) {
            restingFieldBottom = measuredFieldBottom
        }
        // Positive lifts the card, negative lets it back down. Down matters on iOS: the system
        // shortens the scene there, the card is centred in what is left and ends up further above
        // the keyboard than asked for. It may only come down by as much as it hangs off the top,
        // so a card that already fits never drifts below where it was centred.
        val overhangTop = maxOf(0, (cardHeight - containerHeight) / 2)
        // A keyboard cannot cover more than the box it is drawn over, and a platform saying it
        // does is not to be believed — Aurora reports the screen's whole long side, 2000 against a
        // 1200-tall content, which unclamped throws the card a screen and a half off the top.
        // Clamped, an overblown number lifts the card as far as it can go and no further.
        val overlapInBox = overlapPx.coerceIn(0, containerHeight)
        val shiftPx = restingFieldBottom
            ?.let { (it + gapPx - (containerBottom - overlapInBox)).coerceAtLeast(-overhangTop) }
            ?: 0

        // iOS shortens the scene when the keyboard opens, which re-lays out the card underneath us,
        // so a resting position taken before that describes a layout that no longer exists. Retake
        // it whenever the scene resizes: measured + shift is where the field would be with nothing
        // shifted, so this stays a fixed point rather than chasing itself.
        LaunchedEffect(containerBottom) {
            if (measuredFieldBottom != null) restingFieldBottom = measuredFieldBottom + shiftPx
        }

        // Whether the card moves or merely gets smaller is not something a photograph settles:
        // the sizes and the shift together say which. Reported on change only, so an idle modal
        // stays quiet.
        LaunchedEffect(containerHeight, cardHeight, overlapPx, shiftPx) {
            Napier.i(tag = MODAL_TAG) {
                "container ${containerHeight}px, card ${cardHeight}px, " +
                    "overlap ${overlapPx}px (used ${overlapInBox}px), shift ${shiftPx}px"
            }
        }

        val focusManager = LocalFocusManager.current

        // The keyboard can also go away without the app being told: on Aurora it is swiped down or
        // dismissed from its own key, and the fork reports neither. The field keeps its focus, the
        // fork keeps the maliit session open, and that mismatch is where the tablet freezes —
        // testing hits it on exactly this gesture. The overlap dropping back to zero after the
        // keyboard had been up is the only notice there is, so treat it as the end of editing and
        // catch up: drop the focus, and close the session the fork will not close.
        //
        // Only after the keyboard has actually been seen, or the zero every field starts out with
        // would clear the focus the moment it was taken. Nothing here fires on iOS, where the
        // overlap is zero throughout.
        var keyboardHasBeenUp by remember { mutableStateOf(false) }
        if (overlapPx > 0) keyboardHasBeenUp = true
        val keyboardGone = keyboardHasBeenUp && overlapPx == 0
        LaunchedEffect(keyboardGone) {
            if (!keyboardGone) return@LaunchedEffect
            keyboardHasBeenUp = false
            val stillEditing = focusedFieldBottom.value != null
            Napier.i(tag = MODAL_TAG) { "keyboard gone on its own, field still focused: $stillEditing" }
            if (!stillEditing) return@LaunchedEffect
            // Dropping the focus is all this does: the field closes the keyboard session from its
            // own focus-lost branch, so closing it here as well only meant a second trip into the
            // fork. Which is what the wrapping is for — clearFocus() unwinds the fork's input
            // method, and the fork is the part already known to be broken here. A native crash
            // inside it is not catchable this way; the log line above is then the last thing seen.
            runCatching {
                focusManager.clearFocus()
            }.onFailure {
                Napier.e(throwable = it, tag = MODAL_TAG) { "dropping the focus failed" }
            }
        }

        // A tap on the dim takes one step back, not two: with the keyboard up it puts the keyboard
        // away and leaves the modal, and only closes the modal once there is no keyboard to close.
        // iOS has no dismiss key on its keyboard, so without this the only way out of the keyboard
        // is to close the whole dialog.
        val onDimTap: () -> Unit = {
            if (focusedFieldBottom.value != null) focusManager.clearFocus() else onDismiss()
        }

        CompositionLocalProvider(LocalFocusedFieldBottom provides focusedFieldBottom) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onSizeChanged {
                        containerHeight = it.height
                        fullHeight = maxOf(fullHeight, it.height)
                    }
                    .onGloballyPositioned {
                        containerBottom = (it.positionInWindow().y + it.size.height).roundToInt()
                    }
                    .background(Color.Black.copy(alpha = 0.9f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDimTap,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .then(
                            if (fullHeight > 0) {
                                Modifier.requiredHeightIn(max = with(density) { fullHeight.toDp() })
                            } else {
                                Modifier
                            }
                        )
                        // A draw-time translation, not a layout offset: the field reports its
                        // position from layout, and moving it there would feed our own shift back
                        // into the number we derive the shift from.
                        .graphicsLayer { translationY = -shiftPx.toFloat() }
                        .onSizeChanged { cardHeight = it.height }
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {},
                        ),
                ) {
                    content()
                }
            }
        }
    }
}
