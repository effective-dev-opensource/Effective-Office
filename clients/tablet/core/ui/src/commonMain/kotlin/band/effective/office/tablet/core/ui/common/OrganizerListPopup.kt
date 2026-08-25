package band.effective.office.tablet.core.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import band.effective.office.tablet.core.ui.inactivity.InactivityTracker

// Gap between the field and the expanded list, in px.
private const val LIST_GAP_PX = 8

/**
 * The popup that holds the expanded organizer list. The list is the same everywhere, its placement
 * is not: [content] receives the modifier that positions it, empty where the popup positions
 * itself. [anchorCoords] is the row around the field, `null` until it has been laid out once.
 */
@Composable
internal expect fun OrganizerListPopup(
    anchorCoords: LayoutCoordinates?,
    content: @Composable (Modifier) -> Unit,
)

/**
 * Android and iOS keep the popup in the same scene as the field, so an ordinary position provider
 * anchors it and opens it upward. It lives in commonMain because both platforms share it word for
 * word and there is no source set common to just those two.
 */
@Composable
internal fun AnchoredOrganizerList(
    anchorCoords: LayoutCoordinates?,
    content: @Composable (Modifier) -> Unit,
) {
    val popupPositionProvider = remember(anchorCoords) {
        object : PopupPositionProvider {
            override fun calculatePosition(
                anchorBounds: IntRect,
                windowSize: IntSize,
                layoutDirection: LayoutDirection,
                popupContentSize: IntSize
            ): IntOffset {
                // anchorBounds is the popup call site's rect on screen — the row that carries
                // the field sits above it, so its top is anchorBounds.top minus the row's height.
                // Reading it through positionInWindow instead would mix window and screen spaces
                // on Android, where the popup runs in its own window and drifts by the status bar.
                return if (anchorCoords != null) {
                    val rowHeight = anchorCoords.size.height
                    val y = anchorBounds.top - rowHeight - popupContentSize.height - LIST_GAP_PX
                    IntOffset(anchorBounds.left, y.coerceAtLeast(0))
                } else {
                    IntOffset.Zero
                }
            }
        }
    }
    Popup(
        popupPositionProvider = popupPositionProvider,
        onDismissRequest = { },
    ) {
        InactivityTracker {
            content(Modifier)
        }
    }
}
