package band.effective.office.tablet.core.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf

/**
 * How many pixels of the app's own content area the on-screen keyboard covers.
 *
 * The one thing that is genuinely per-platform here, because they disagree about who moves what:
 *
 * - **Android** draws edge to edge and never resizes the window, so the keyboard covers the content.
 *   The ime inset is measured from the bottom of the window and includes the navigation bar, which
 *   the content is already padded away from — hence the subtraction.
 * - **iOS** shortens the Compose scene to the area above the keyboard before anything of ours runs.
 *   Nothing is covered any more — the room is simply gone — so the answer is zero.
 * - **Aurora** reports nothing yet. The fork's maliit session knows the height
 *   (`Keyboard.listenState { _, event -> event.height }`), but it is not wired up.
 */
@Composable
expect fun softKeyboardOverlapPx(): Int

/**
 * Bottom edge of the text field currently being typed into, in window pixels, or `null` when
 * nothing is focused.
 *
 * A field writes here while it holds focus so the modal above it knows how far to move: what has to
 * clear the keyboard is the field, not the card around it, and only the field knows where it is.
 */
val LocalFocusedFieldBottom = compositionLocalOf<MutableState<Int?>?> { null }
