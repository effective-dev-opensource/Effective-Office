package band.effective.office.tablet.core.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.Dp
import kotlin.math.roundToInt

/**
 * The tablet is a landscape-locked kiosk, but the Aurora window has no orientation handling and
 * on a portrait screen the whole UI — laid out horizontally — gets squashed. On Aurora we force
 * landscape by rotating the content; on Android and iOS orientation is left to the system.
 *
 * The rotation itself only happens when the window really is portrait (see [ForcedLandscape]),
 * so the flag breaks nothing on a landscape screen.
 */
expect val forceLandscape: Boolean

/**
 * Whether the platform renders a `Popup` as a scene of its own.
 *
 * The Aurora fork does: a popup gets its own scene in the untouched window — unrotated and with
 * the system density — so nothing applied at the root reaches it, its position provider cannot
 * anchor against the content layout, and the layer has to be stretched and positioned by hand.
 * Android and iOS render a popup in the same scene, where the ordinary anchored positioning works.
 *
 * This is deliberately not [forceLandscape]: the popup problem is scene isolation, and rotation is
 * only one of its consequences.
 */
expect val popupIsSeparateScene: Boolean

/**
 * The reference short side of the window, in dp, that [ScaledUiDensity] normalises the UI to.
 *
 * The Compose scene density cannot be set on Aurora: the fork creates the scene as
 * `ComposeScene(density = Density(ru.auroraos.kmp.window.contentScale.toFloat()))`, and
 * `contentScale` arrives from the system together with the window. So the dp space is fixed here
 * instead, at the number that lays Aurora out like the reference Android tablet — the two devices
 * are both 1200 px on the short side, so the baseline is derived from that rather than picked for
 * a familiar screen size. The arithmetic is in the linux actual and in AURORA.md.
 *
 * On Android and iOS the system provides the density and scaling is off ([Dp] == 0).
 */
expect val uiScaleBaseline: Dp

/**
 * How many pixels of the app's own content area the on-screen keyboard covers.
 *
 * The platforms disagree about who moves what when a keyboard opens:
 *
 * - **Android** draws edge to edge and never resizes the window, so the keyboard covers the content.
 *   The ime inset is measured from the bottom of the window and includes the navigation bar, which
 *   the content is already padded away from — hence the subtraction in the actual.
 * - **iOS** shortens the Compose scene to the area above the keyboard before anything of ours runs.
 *   Nothing is covered any more — the room is simply gone — so the answer is zero.
 * - **Aurora** covers the content the way Android does, but nothing tells the app about it — the
 *   fork reports no keyboard insets at all. The height is taken from the maliit session instead,
 *   which has to be asked rather than listened to; the linux actual says why.
 */
@Composable
expect fun softKeyboardOverlapPx(): Int

/**
 * Puts the on-screen keyboard away, for the platform that will not do it on its own.
 *
 * Only Aurora has work here. The fork starts a maliit session when a field takes focus and then
 * parks in `awaitCancellation()` with no `finally`, so the session is never stopped: the field
 * stops being edited and maliit still believes it is feeding one. That leftover session is the
 * likeliest trigger for input being routed away from the app for good — the freeze that testing
 * keeps hitting on the tablet. Closing it by hand is what the missing `finally` would have done.
 *
 * Android and iOS take their keyboard down together with the focus, so their actuals do nothing.
 *
 * Safe to call from anywhere, including where a failure would be awkward: the Aurora actual logs
 * whatever the fork throws under the `SoftKeyboard` tag and returns.
 */
expect fun closeSoftKeyboard()

/**
 * Tells the platform a text field has just been pressed — that a keyboard is on its way — before
 * the platform has any way of knowing.
 *
 * Only Aurora listens. The fork starts the maliit session synchronously and only grants focus at
 * the end of it, which takes a second or two; the keyboard is already rising through all of it
 * while focus, `Keyboard.isOpen()` and the state event alike still say there is none. The press is
 * the one signal that comes before the keyboard, so [softKeyboardOverlapPx] takes it as notice and
 * answers optimistically for a few seconds; if no keyboard follows, the notice expires by itself.
 *
 * Android and iOS report their insets as the keyboard moves, so their actuals do nothing.
 */
expect fun noteSoftKeyboardExpected()

/**
 * The contract between a modal host and the editable content inside it. `null` outside a modal.
 *
 * Everything positional in it is measured field-against-host, never through window coordinates. On
 * Aurora there is a 90° rotation between the window and the content ([ForcedLandscape]) and
 * `positionInWindow()` goes through it: the window-Y of a point inside the rotated content is its
 * content-X. The tablet showed exactly that — a field whose bottom sits around 860px in the
 * content reported 557, which is where it sits across, and the host duly decided the keyboard was
 * not in its way. A measurement between two nodes on the same side of the rotation cancels it out;
 * on Android and iOS the two frames coincide anyway.
 */
@Stable
class ModalHostState {
    /**
     * Bottom edge of the field currently being edited, in [containerCoords] space, or `null` when
     * none. A field writes it while it holds focus so the host knows how far to lift the card:
     * what has to clear the keyboard is the field, and only the field knows where it is.
     */
    var focusedFieldBottom: Int? by mutableStateOf(null)

    /** The host's full-screen container box — the frame [focusedFieldBottom] is measured in. */
    var containerCoords: LayoutCoordinates? by mutableStateOf(null)

    /**
     * The card box — what the keyboard shift moves, and what [overlay] content is composed into.
     * Anchoring overlay content field-against-card keeps every transform on both sides of the
     * measurement, where it cancels: the shift and the Aurora rotation alike. Anchoring against
     * anything outside the card would mean adding the shift back by hand, at the mercy of when
     * the layer matrices update.
     */
    var cardCoords: LayoutCoordinates? by mutableStateOf(null)

    /**
     * Content drawn on top of the card, in the same scene. On Android and iOS such content goes
     * into a `Popup` and this slot stays empty. On Aurora a popup is a scene of its own — a second
     * window the fork creates on demand, which takes a visible pause to appear and has to be aimed
     * across coordinate spaces that disagree about rotation and density. Content in the slot shows
     * up on the frame it is set and inherits the rotation, density and input handling already
     * applied around it.
     */
    var overlay: (@Composable () -> Unit)? by mutableStateOf(null)
}

val LocalModalHost = compositionLocalOf<ModalHostState?> { null }

/**
 * Bottom edge of [field] in [container]'s space, for [ModalHostState.focusedFieldBottom]. Falls
 * back to window space when there is no container to measure against, which happens only in
 * previews and in hosts that do not move for the keyboard.
 */
fun fieldBottomPx(container: LayoutCoordinates?, field: LayoutCoordinates): Int =
    if (container != null && container.isAttached) {
        container.localPositionOf(field, Offset(0f, field.size.height.toFloat())).y.roundToInt()
    } else {
        (field.positionInWindow().y + field.size.height).roundToInt()
    }
