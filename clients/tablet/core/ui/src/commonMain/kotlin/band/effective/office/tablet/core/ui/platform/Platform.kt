package band.effective.office.tablet.core.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.Dp
import kotlin.time.Duration.Companion.seconds

expect fun getCurrentLanguageCode(): String

/**
 * Whether the content has to be turned landscape by hand. The tablet is a landscape-locked kiosk
 * and the Aurora window arrives portrait; Android and iOS leave orientation to the system.
 */
expect val forceLandscape: Boolean

/**
 * Padding for Aurora's status bar, applied by [AuroraWindowFrame] inside the rotation. Zero on
 * Android and iOS, where `systemBarsPadding()` covers the system bars instead.
 */
expect val statusBarInset: Dp

/**
 * The reference short side, in dp, that [ScaledUiDensity] normalises the window to. Zero on Android
 * and iOS, where the system hands over a density worth keeping and nothing has to be normalised.
 */
expect val uiScaleBaseline: Dp

/**
 * Re-applies around a scene of its own what [AuroraWindowFrame] applies around the root: the fork
 * draws a `Dialog` as a separate scene in the untouched window, which inherits none of it.
 */
@Composable
expect fun DialogSceneFrame(content: @Composable () -> Unit)

/**
 * How many pixels of the app's own content area the on-screen keyboard covers. Zero on iOS, where
 * the host shortens the scene instead — see "The on-screen keyboard" in
 * clients/tablet/core/ui/README.md.
 */
@Composable
expect fun softKeyboardOverlapPx(): Int

/**
 * Tells the platform a field has just been pressed, before it can know. Only Aurora listens, where
 * every honest sign of a keyboard arrives after the keyboard itself.
 */
expect fun noteSoftKeyboardExpected()

/**
 * How long a press stands as a promise of a keyboard and of the focus that comes with it. One
 * number for both, or the shorter of the two expires mid-handshake and drops what the longer is
 * still holding up; the Aurora handshake has been seen to take six seconds.
 */
internal val SOFT_KEYBOARD_PRESS_GRACE = 10.seconds

/**
 * What a modal host offers the content inside it; `null` outside one. Anything positional here is
 * measured against the card, never through window coordinates: on Aurora `positionInWindow()` maps
 * up through [ForcedLandscape], so the window-Y of a rotated node is its content-X.
 */
@Stable
class ModalHostState {
    /** The card box, which everything in [overlay] is anchored against so both sides move alike. */
    var cardCoords: LayoutCoordinates? by mutableStateOf(null)

    /**
     * Content drawn over the card, inside the card's own box and in the same scene. Stays empty
     * where such content goes into a `Popup` instead.
     */
    var overlay: (@Composable () -> Unit)? by mutableStateOf(null)
}

val LocalModalHost = compositionLocalOf<ModalHostState?> { null }
