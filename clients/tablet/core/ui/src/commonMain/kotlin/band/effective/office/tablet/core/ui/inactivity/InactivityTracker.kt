package band.effective.office.tablet.core.ui.inactivity

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Watches [content] for any user input and keeps [InactivityTracking] fed, without taking anything
 * away from the content itself.
 *
 * Three things about this are load-bearing:
 *
 * - **[PointerEventPass.Initial], never `Main`.** The initial pass runs root-down, before children
 *   get their turn. On the main pass a `clickable` child would already have consumed the event and
 *   this wrapper would see nothing.
 * - **Nothing is consumed.** No `consume()` on the changes, and `onPreviewKeyEvent` returns false —
 *   this is an observer, and the moment it starts swallowing input it becomes a bug.
 * - **One instance per scene layer.** A `Popup` and a `dialog<>` are separate windows on Android
 *   and separate scenes in the Aurora fork, so neither sees a wrapper installed at the root. The
 *   layers are the same three that already re-apply `ForcedLandscape` and `ScaledUiDensity`.
 *
 * [modifier] is a parameter rather than a built-in `fillMaxSize` because the popup layer is sized
 * to its content on Android — stretching it there would break the dropdown's layout.
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
