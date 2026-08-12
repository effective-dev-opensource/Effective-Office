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

/** How much air to leave between the field being typed into and the top of the keyboard. */
private val FIELD_TO_KEYBOARD_GAP = 8.dp

/** Same log tag family as `SoftKeyboard` and `OrganizerPicker`, so one grep reads the whole story. */
private const val MODAL_TAG = "ModalHost"

/** What the keyboard costs the modal card. Applied by the host's card box. */
internal data class ModalKeyboardShift(
    /**
     * How far up the card has to be drawn. Zero leaves it where it was centred; negative lets it
     * back down, which is what iOS needs — the system shortens the scene there, so the card is
     * centred in what is left and ends up further above the keyboard than asked for.
     *
     * To be drawn with, not laid out with; the call site says why.
     */
    val offsetPx: Int,
    /**
     * The tallest the host box has been, and the height the card should go on being measured
     * against; [Dp.Unspecified] before the first measurement, which `requiredHeightIn` reads as no
     * constraint at all.
     *
     * This is the other half of not padding the box the card sits in. On iOS the system shortens
     * the scene when the keyboard opens, and a card measured against the shorter box shrinks — its
     * own content then clips from the inside instead of the card being clipped by the screen edge.
     * It cannot simply be measured unbounded either: the card scrolls inside, and Compose refuses
     * an infinite height above a scrolling container. So it is measured against the tallest the box
     * has ever been, which is the screen before the keyboard took its share.
     */
    val maxCardHeight: Dp,
)

/**
 * Works out what the on-screen keyboard costs the modal card — and ends editing when the keyboard
 * goes away without the app being told.
 *
 * What has to clear the keyboard is the focused field, not the card around it: aim for the field's
 * bottom edge sitting [FIELD_TO_KEYBOARD_GAP] above the keyboard, and move the card by however much
 * that costs. Nothing focused, or the field already high enough — no shift.
 *
 * Everything positional lives in the host container's own space — see [ModalHostState] for why
 * window space is unusable on Aurora. The field reports its bottom against the container, and the
 * top of the keyboard is [containerHeightPx] minus the overlap, which holds because the container
 * fills the scene down to the same edge the keyboard rises from. The sizes are passed in rather
 * than read off the window: on iOS `LocalWindowInfo.containerSize` comes back with the sides
 * swapped — 1668 as the height of a 1668x2420 portrait screen — and everything derived from it
 * lands nowhere near the keyboard.
 *
 * The withdrawal watch lives here rather than in the host because the overlap belongs to whoever
 * reads it: on Aurora `softKeyboardOverlapPx()` is a 100 ms poll of the maliit session, and a
 * second reader would mean a second poll of the one binding already known to be fragile.
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
    // Deriving it from the live position instead would feed the shift back into its own input:
    // move the card, the field moves with it, the number shrinks, the shift shrinks — and the
    // card jitters between two positions. The keyboard is still animating at this point, so the
    // shift keeps recomputing against a moving keyboardTop, which is fine — that end is real.
    var restingFieldBottom by remember { mutableStateOf<Int?>(null) }
    val measuredFieldBottom = anchor.focusedFieldBottom
    if (measuredFieldBottom == null) {
        restingFieldBottom = null
    } else if (restingFieldBottom == null) {
        restingFieldBottom = measuredFieldBottom
    }

    // The card may only come down by as much as it hangs off the top, so a card that already fits
    // never drifts below where it was centred.
    val overhangTop = maxOf(0, (cardHeightPx - containerHeightPx) / 2)
    // A keyboard cannot cover more than the box it is drawn over, and a platform saying it does is
    // not to be believed — Aurora reports the screen's whole long side, 2000 against a 1200-tall
    // content, which unclamped throws the card a screen and a half off the top. Clamped, an
    // overblown number lifts the card as far as it can go and no further.
    val overlapInBox = overlapPx.coerceIn(0, containerHeightPx)
    val shiftPx = restingFieldBottom
        ?.let { (it + gapPx - (containerHeightPx - overlapInBox)).coerceAtLeast(-overhangTop) }
        ?: 0

    // iOS shortens the scene when the keyboard opens, which re-lays out the card underneath us,
    // so a resting position taken before that describes a layout that no longer exists. Retake
    // it whenever the scene resizes: measured + shift is where the field would be with nothing
    // shifted, so this stays a fixed point rather than chasing itself.
    LaunchedEffect(containerHeightPx) {
        if (measuredFieldBottom != null) restingFieldBottom = measuredFieldBottom + shiftPx
    }

    // Whether the card moves or merely gets smaller is not something a photograph settles:
    // the sizes and the shift together say which. Reported on change only, so an idle modal
    // stays quiet.
    LaunchedEffect(containerHeightPx, cardHeightPx, overlapPx, shiftPx) {
        Napier.i(tag = MODAL_TAG) {
            "container ${containerHeightPx}px, card ${cardHeightPx}px, " +
                "overlap ${overlapPx}px (used ${overlapInBox}px), shift ${shiftPx}px"
        }
    }

    KeyboardWithdrawalWatch(anchor = anchor, overlapPx = overlapPx)

    return ModalKeyboardShift(
        offsetPx = shiftPx,
        maxCardHeight = if (fullHeightPx > 0) with(density) { fullHeightPx.toDp() } else Dp.Unspecified,
    )
}

/**
 * Ends editing when the keyboard goes away behind the app's back.
 *
 * On Aurora it is swiped down or dismissed from its own key, and the fork reports neither. The
 * field keeps its focus, the fork keeps the maliit session open, and that mismatch is where the
 * tablet freezes — testing hits it on exactly this gesture. [overlapPx] dropping back to zero after
 * the keyboard had been up is the only notice there is, so treat it as the end of editing and catch
 * up: drop the focus, which is the one door everything else leaves editing through, and from which
 * the field closes the session the fork will not close.
 *
 * Only after the keyboard has actually been seen, or the zero every field starts out with would
 * clear the focus the moment it was taken. Nothing here fires on iOS, where the overlap is zero
 * throughout.
 */
@Composable
private fun KeyboardWithdrawalWatch(anchor: ModalHostState, overlapPx: Int) {
    val focusManager = LocalFocusManager.current

    var keyboardHasBeenUp by remember { mutableStateOf(false) }
    if (overlapPx > 0) keyboardHasBeenUp = true
    val keyboardGone = keyboardHasBeenUp && overlapPx == 0

    LaunchedEffect(keyboardGone) {
        if (!keyboardGone) return@LaunchedEffect
        keyboardHasBeenUp = false
        val stillEditing = anchor.focusedFieldBottom != null
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
}
