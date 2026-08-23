package band.effective.office.tablet.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.platform.ModalHostState
import band.effective.office.tablet.core.ui.platform.softKeyboardOverlapPx
import io.github.aakira.napier.Napier

/** Air left between the field being typed into and the top of the keyboard. */
private val FIELD_TO_KEYBOARD_GAP = 8.dp

private const val MODAL_TAG = "ModalHost"

/** What the keyboard costs the modal card, for the host's card box to apply. */
internal data class ModalKeyboardShift(
    /** How far up the card is drawn. Negative lets it back down, which is what iOS asks for. */
    val offsetPx: Int,
    /**
     * The tallest the host box has been, and what the card goes on being measured against;
     * [Dp.Unspecified] before the first measurement, which `requiredHeightIn` takes as no cap.
     */
    val maxCardHeight: Dp,
)

/**
 * Works out what the on-screen keyboard costs the modal card, and ends editing when the keyboard
 * goes away without the app being told. What has to clear the keyboard is the focused field, not
 * the card around it — see Navigation in clients/tablet/composeApp/README.md.
 */
@Composable
internal fun modalKeyboardShift(
    anchor: ModalHostState,
    containerHeightPx: Int,
    cardHeightPx: Int,
): ModalKeyboardShift {
    val density = LocalDensity.current
    val overlapPx = softKeyboardOverlapPx()
    val gapPx = with(density) { FIELD_TO_KEYBOARD_GAP.roundToPx() }

    var fullHeightPx by remember { mutableStateOf(0) }
    if (containerHeightPx > fullHeightPx) fullHeightPx = containerHeightPx

    // Where the field sits with nothing shifted, captured once while the shift is still zero.
    // Reading the live position instead would feed the shift back into its own input.
    var restingFieldBottom by remember { mutableStateOf<Int?>(null) }
    val measuredFieldBottom = anchor.focusedFieldBottom
    if (measuredFieldBottom == null) {
        restingFieldBottom = null
    } else if (restingFieldBottom == null) {
        restingFieldBottom = measuredFieldBottom
    }

    // The card may only come down by as much as it hangs off the top, so one that already fits
    // never drifts below where it was centred.
    val overhangTop = maxOf(0, (cardHeightPx - containerHeightPx) / 2)
    // A keyboard cannot cover more than the box it is drawn over, whatever the platform says it
    // covers: Aurora's unclamped 2000 against a 1200-tall content throws the card off the top.
    val overlapInBox = overlapPx.coerceIn(0, containerHeightPx)
    val shiftPx = restingFieldBottom
        ?.let { (it + gapPx - (containerHeightPx - overlapInBox)).coerceAtLeast(-overhangTop) }
        ?: 0

    // iOS re-lays out the card underneath us when it shortens the scene, so a resting position
    // taken before that describes a layout that is gone. measured + shift is where the field would
    // be with nothing shifted, which makes retaking it a fixed point rather than a chase.
    LaunchedEffect(containerHeightPx) {
        if (measuredFieldBottom != null) restingFieldBottom = measuredFieldBottom + shiftPx
    }

    LaunchedEffect(containerHeightPx, cardHeightPx, overlapPx, shiftPx) {
        Napier.i(tag = MODAL_TAG) {
            "container ${containerHeightPx}px, card ${cardHeightPx}px, " +
                "overlap ${overlapPx}px (used ${overlapInBox}px), shift ${shiftPx}px"
        }
    }

    KeyboardWithdrawalWatch(anchor = anchor, overlapPx = overlapPx)

    return ModalKeyboardShift(
        offsetPx = shiftPx,
        maxCardHeight = if (fullHeightPx > 0) {
            with(density) { fullHeightPx.toDp() }
        } else {
            Dp.Unspecified
        },
    )
}

/**
 * Ends editing when the keyboard goes away behind the app's back — swiped down or dismissed from
 * its own key, neither of which the Aurora fork reports. The overlap dropping back to zero after
 * the keyboard had been up is the only notice there is.
 */
@Composable
private fun KeyboardWithdrawalWatch(anchor: ModalHostState, overlapPx: Int) {
    val focusManager = LocalFocusManager.current

    // Only after a keyboard has actually been seen, or the zero every field starts out with would
    // clear the focus the moment it was taken.
    var keyboardHasBeenUp by remember { mutableStateOf(false) }
    if (overlapPx > 0) keyboardHasBeenUp = true
    val keyboardGone = keyboardHasBeenUp && overlapPx == 0

    LaunchedEffect(keyboardGone) {
        if (!keyboardGone) return@LaunchedEffect
        keyboardHasBeenUp = false
        if (anchor.focusedFieldBottom == null) return@LaunchedEffect
        // Dropping the focus is all this does; the field closes the session from its own focus-lost
        // branch. clearFocus() unwinds the fork's input method, which is the broken part here.
        runCatching {
            focusManager.clearFocus()
        }.onFailure {
            Napier.e(throwable = it, tag = MODAL_TAG) { "dropping the focus failed" }
        }
    }
}
