package band.effective.office.tablet.core.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp

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
 * - **Aurora** answers zero as well, but for a different reason: the fork reports no keyboard insets
 *   at all. Its maliit session does know the height, and wiring that up is what would make the
 *   modal move there too.
 */
@Composable
expect fun softKeyboardOverlapPx(): Int

/**
 * Bottom edge of the text field currently being typed into, in window pixels, or `null` when
 * nothing is focused.
 *
 * A field writes here while it holds focus so the modal hosting it knows how far to move: what has
 * to clear the keyboard is the field, not the card around it, and only the field knows where it is.
 */
val LocalFocusedFieldBottom = compositionLocalOf<MutableState<Int?>?> { null }
