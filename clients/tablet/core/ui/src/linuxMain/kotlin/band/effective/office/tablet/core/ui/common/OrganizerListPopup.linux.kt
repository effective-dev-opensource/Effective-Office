package band.effective.office.tablet.core.ui.common

import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import io.github.aakira.napier.Napier
import kotlin.math.roundToInt

// Gap between the field and the expanded list, in px (px do not depend on a substituted density).
private const val LIST_GAP_PX = 8

/** Same log tag family as `OrganizerPicker` and `ModalHost`, so one grep reads the whole story. */
private const val LIST_TAG = "OrganizerList"

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
 * the card, and anchors against the field's row through their nearest shared ancestor, where every
 * transform between them — the keyboard shift, the rotation — cancels.
 *
 * The row and not the field inside it: the list is sized to the row, so anchoring it to the field
 * would place it 20.dp — the row's horizontal padding — to the right of where it is drawn from,
 * which is what had it hanging off the card's right edge.
 *
 * There is deliberately no fallback for a host without a slot: the only user of this is the
 * organizer field, the only user of that is the booking editor, and the editor is only ever
 * composed inside `ModalHost`. A second path would be one nobody runs.
 */
@Composable
internal actual fun OrganizerListPopup(
    anchorCoords: LayoutCoordinates?,
    content: @Composable (Modifier) -> Unit,
) {
    val modalHost = LocalModalHost.current ?: return
    // Latest values behind stable reads, so the slot is written once per open list rather than on
    // every recomposition that brings a new lambda or new coordinates.
    val coords by rememberUpdatedState(anchorCoords)
    val body by rememberUpdatedState(content)
    DisposableEffect(modalHost) {
        modalHost.overlay = {
            var listSize by remember { mutableStateOf(IntSize.Zero) }
            val anchor = modalHost.cardCoords?.takeIf { it.isAttached }?.let { card ->
                coords?.takeIf { it.isAttached }?.let { field ->
                    card.localPositionOf(field, Offset.Zero)
                }
            } ?: Offset.Zero
            // Where the anchor actually lands, against the two boxes it is derived from. A
            // screenshot cannot separate "the list is offset" from "the list is the wrong width" —
            // these four numbers can, and the popup section of AURORA.md is there because eyeballing
            // that difference once produced the wrong answer.
            //
            // Sizes written with spaces around the separator, never as `0x0`: the deploy plugin
            // reads the app's output looking for a native backtrace, takes a bare `0x0` for an
            // address and ends the run with "Application crashed with critical errors" over a line
            // that is only saying the list has not been measured yet.
            val anchorSize = coords?.takeIf { it.isAttached }?.size
            val cardSize = modalHost.cardCoords?.takeIf { it.isAttached }?.size
            LaunchedEffect(anchor, listSize, anchorSize, cardSize) {
                Napier.i(tag = LIST_TAG) {
                    "anchor ${anchor.x.roundToInt()},${anchor.y.roundToInt()} in card " +
                        "${cardSize?.width} x ${cardSize?.height}; " +
                        "row ${anchorSize?.width} x ${anchorSize?.height}; " +
                        "list ${listSize.width} x ${listSize.height}"
                }
            }
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
