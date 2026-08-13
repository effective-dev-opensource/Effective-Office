package band.effective.office.tablet.core.ui.inactivity

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Feeds [InactivityTracking] with any input inside [content], consuming nothing.
 * One instance per scene layer: a popup or a dialog is a window of its own and never reaches the
 * instance installed at the root.
 */
@Composable
fun InactivityTracker(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        // The initial pass runs root-down; on the main pass a clickable child
                        // would already have taken the event.
                        awaitPointerEvent(PointerEventPass.Initial)
                        InactivityTracking.onUserInteraction()
                    }
                }
            }
            .onPreviewKeyEvent {
                InactivityTracking.onUserInteraction()
                false
            },
    ) {
        content()
    }
}
