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

/** Gap between the row and the list above it, in px, so a substituted density cannot stretch it. */
private const val SLOT_LIST_GAP_PX = 8

/**
 * The list goes into the modal host's slot — see the Aurora window model in
 * clients/tablet/core/ui/README.md. No fallback for a host without one on purpose: the list's only
 * user is the booking editor, and the editor is only ever composed inside a modal host.
 */
@Composable
internal actual fun OrganizerListPopup(
    anchorCoords: LayoutCoordinates?,
    content: @Composable (Modifier) -> Unit,
) {
    val modalHost = LocalModalHost.current ?: return
    val coords by rememberUpdatedState(anchorCoords)
    val body by rememberUpdatedState(content)
    DisposableEffect(modalHost) {
        modalHost.overlay = {
            var listSize by remember { mutableStateOf(IntSize.Zero) }
            val card = modalHost.cardCoords?.takeIf { it.isAttached }
            val row = coords?.takeIf { it.isAttached }
            val anchor = if (card != null && row != null) {
                card.localPositionOf(row, Offset.Zero)
            } else {
                Offset.Zero
            }
            body(
                Modifier
                    .offset {
                        IntOffset(
                            x = anchor.x.roundToInt(),
                            y = (anchor.y.roundToInt() - listSize.height - SLOT_LIST_GAP_PX)
                                .coerceAtLeast(0),
                        )
                    }
                    .onSizeChanged { listSize = it },
            )
        }
        onDispose { modalHost.overlay = null }
    }
}
