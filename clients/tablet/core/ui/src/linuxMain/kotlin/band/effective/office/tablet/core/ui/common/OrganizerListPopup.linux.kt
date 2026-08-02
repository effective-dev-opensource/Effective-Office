package band.effective.office.tablet.core.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import band.effective.office.tablet.core.ui.platform.ScaledUiDensity
import kotlin.math.roundToInt

// Gap between the field and the expanded list, in px (px do not depend on a substituted density).
private const val LIST_GAP_PX = 8

/**
 * Aurora: the popup is a scene of its own in the fork's untouched window, so the position provider
 * cannot anchor against the content layout and the list is placed by hand instead.
 */
@Composable
internal actual fun OrganizerListPopup(
    textFieldCoords: LayoutCoordinates?,
    content: @Composable (Modifier) -> Unit,
) {
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
        // rotation and the inactivity tracker are re-applied here (same as for modals in
        // DialogBackgroundDim).
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
