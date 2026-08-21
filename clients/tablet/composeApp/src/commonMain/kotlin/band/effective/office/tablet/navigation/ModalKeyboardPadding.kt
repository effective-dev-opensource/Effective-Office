package band.effective.office.tablet.navigation

import androidx.compose.ui.Modifier

/**
 * Keeps a modal overlay clear of the on-screen keyboard. A no-op where the host already shortens
 * the scene for it — subtracting the keyboard twice collapses the card. See Navigation in
 * clients/tablet/composeApp/README.md.
 */
expect fun Modifier.modalKeyboardPadding(): Modifier
