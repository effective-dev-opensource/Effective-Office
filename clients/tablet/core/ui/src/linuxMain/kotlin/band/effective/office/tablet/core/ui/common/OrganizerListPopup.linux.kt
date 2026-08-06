package band.effective.office.tablet.core.ui.common

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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import band.effective.office.tablet.core.ui.platform.LocalModalHost
import kotlin.math.roundToInt

// Gap between the field and the expanded list, in px (px do not depend on a substituted density).
private const val LIST_GAP_PX = 8

/**
 * Aurora: the list is not a popup at all, it goes into the modal's own scene.
 *
 * A `Popup` here is a scene of its own — a second window the fork creates on demand. It takes a
 * visible pause to come up, arrives without the rotation, density and inactivity tracking applied
 * around everything else, and can only be aimed by carrying coordinates into a scene that disagrees
 * with this one about which way is down: `positionInWindow()` maps up through the `ForcedLandscape`
 * layer, so what it calls Y is the node's content-X, and the list lands off to the side. That was
 * the arrangement here until the modal host grew a slot to render into.
 *
 * In the slot the list shows up on the frame it opens, inherits everything already applied around
 * the card, and anchors against the field through their nearest shared ancestor, where every
 * transform between them — the keyboard shift, the rotation — cancels.
 *
 * There is deliberately no fallback for a host without a slot: the only user of this is the
 * organizer field, the only user of that is the booking editor, and the editor is only ever
 * composed inside `ModalHost`. A second path would be one nobody runs.
 */
@Composable
internal actual fun OrganizerListPopup(
    textFieldCoords: LayoutCoordinates?,
    content: @Composable (Modifier) -> Unit,
) {
    val modalHost = LocalModalHost.current ?: return
    // Latest values behind stable reads, so the slot is written once per open list rather than on
    // every recomposition that brings a new lambda or new coordinates.
    val coords by rememberUpdatedState(textFieldCoords)
    val body by rememberUpdatedState(content)
    DisposableEffect(modalHost) {
        modalHost.overlay = {
            var listSize by remember { mutableStateOf(IntSize.Zero) }
            val anchor = modalHost.cardCoords?.takeIf { it.isAttached }?.let { card ->
                coords?.takeIf { it.isAttached }?.let { field ->
                    card.localPositionOf(field, Offset.Zero)
                }
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
}
