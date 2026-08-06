package band.effective.office.tablet.core.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import band.effective.office.tablet.core.ui.inactivity.InactivityTracker

/**
 * The popup that holds the expanded organizer list. The list itself is the same everywhere (see
 * `OrganizerListBody`); how it is placed inside the popup is not, and that is all this covers.
 *
 * [content] receives the modifier that positions the list — empty where the popup positions itself.
 *
 * @param anchorCoords the row around the field, or `null` before the first layout pass. The row
 *   rather than the field itself, because the row is also what the list takes its width from
 *   (`mTextFieldSize`), and a list positioned by one node while sized by another is off by whatever
 *   sits between them — here the row's 20.dp horizontal padding, which had the list hanging 35px
 *   past the card's edge on Aurora.
 */
@Composable
internal expect fun OrganizerListPopup(
    anchorCoords: LayoutCoordinates?,
    content: @Composable (Modifier) -> Unit,
)

/**
 * Android and iOS: the popup lives in the same scene as the field, so it is placed by an ordinary
 * position provider anchored to the field and opening upward.
 *
 * This lives in `commonMain` only because the two platforms share it word for word and there is no
 * source set common to just those two — both actuals do nothing but call it.
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
                return if (anchorCoords != null) {
                    val anchorTop = anchorCoords.positionInWindow().y.toInt()
                    val y = anchorTop - popupContentSize.height
                    IntOffset(anchorBounds.left, y.coerceAtLeast(0) - 60)
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
        // A popup is a window of its own here too, so it needs its own inactivity tracker. No
        // fillMaxSize: this layer is sized to its content.
        InactivityTracker {
            content(Modifier)
        }
    }
}
