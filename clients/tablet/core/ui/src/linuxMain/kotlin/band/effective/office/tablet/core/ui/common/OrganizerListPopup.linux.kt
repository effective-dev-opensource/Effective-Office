package band.effective.office.tablet.core.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import band.effective.office.tablet.core.ui.inactivity.InactivityTracker
import band.effective.office.tablet.core.ui.platform.ForcedLandscape
import band.effective.office.tablet.core.ui.platform.LocalModalHost
import band.effective.office.tablet.core.ui.platform.ScaledUiDensity
import kotlin.math.roundToInt

// Gap between the field and the expanded list, in px (px do not depend on a substituted density).
private const val LIST_GAP_PX = 8

/**
 * Aurora: a `Popup` here is a scene of its own — a second window the fork creates on demand,
 * which takes a visible pause to come up and can only be aimed by translating coordinates between
 * scenes that disagree about rotation and density. So inside a modal the list is not a popup at
 * all: it renders into the modal's overlay slot, in the same scene, where it shows up on the
 * frame it opens and anchors against the layout it shares with the field.
 *
 * The `Popup` path below survives as the fallback for a host without the slot, where the
 * cross-scene reconstruction (rotation, density, inactivity) is still the only option.
 */
@Composable
internal actual fun OrganizerListPopup(
    textFieldCoords: LayoutCoordinates?,
    content: @Composable (Modifier) -> Unit,
) {
    val modalHost = LocalModalHost.current
    if (modalHost != null) {
        // Latest values behind stable state reads, so the slot is written once per open list
        // rather than on every recomposition that brings a new lambda or coordinates.
        val coords by rememberUpdatedState(textFieldCoords)
        val body by rememberUpdatedState(content)
        DisposableEffect(modalHost) {
            modalHost.overlay = {
                var listSize by remember { mutableStateOf(IntSize.Zero) }
                // Field position in the card's space — the box this overlay is composed into.
                // Field and card share every transform (the keyboard shift, the forced rotation),
                // so one localPositionOf between them cancels the lot; see ModalHostState.cardCoords.
                val anchor = modalHost.cardCoords?.takeIf { it.isAttached }?.let { c ->
                    coords?.takeIf { it.isAttached }?.let { f -> c.localPositionOf(f, Offset.Zero) }
                } ?: Offset.Zero
                body(
                    Modifier
                        .offset {
                            // The list opens upward from the field, with a small gap.
                            IntOffset(
                                x = anchor.x.roundToInt(),
                                y = (anchor.y.roundToInt() - listSize.height - LIST_GAP_PX)
                                    .coerceAtLeast(0),
                            )
                        }
                        .onSizeChanged { listSize = it }
                )
            }
            onDispose { modalHost.overlay = null }
        }
        return
    }

    val fullWindowPositionProvider = remember {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset = IntOffset.Zero
        }
    }
    Popup(
        popupPositionProvider = fullWindowPositionProvider,
        onDismissRequest = { },
    ) {
        // A popup window is sized to its content by default, and we move the list
        // ourselves with offset — so the layer is stretched to fill the window, or the
        // offset list would fall outside its own window and be clipped.
        // The popup layer is also a separate scene in the fork's unrotated window, so the
        // rotation and the inactivity tracker are re-applied here (same as for the date/time
        // picker's own Dialog).
        InactivityTracker(modifier = Modifier.fillMaxSize()) {
            ForcedLandscape {
                // Its own scene means the system density too, so re-apply the scale.
                ScaledUiDensity(modifier = Modifier.fillMaxSize()) {
                    var listSize by remember { mutableStateOf(IntSize.Zero) }
                    // positionInWindow() reports coordinates in the UNROTATED content
                    // layout, not in the physical window; the rotation is a drawing effect
                    // and does not touch them, so they are used as-is.
                    val anchor = textFieldCoords?.positionInWindow()?.let {
                        IntOffset(it.x.roundToInt(), it.y.roundToInt())
                    } ?: IntOffset.Zero

                    content(
                        Modifier
                            .offset {
                                // The list opens upward from the field, with a small gap.
                                IntOffset(
                                    x = anchor.x,
                                    y = (anchor.y - listSize.height - LIST_GAP_PX)
                                        .coerceAtLeast(0),
                                )
                            }
                            .onSizeChanged { listSize = it }
                    )
                }
            }
        }
    }
}
