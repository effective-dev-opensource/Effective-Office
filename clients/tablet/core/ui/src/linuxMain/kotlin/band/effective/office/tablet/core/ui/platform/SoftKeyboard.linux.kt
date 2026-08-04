package band.effective.office.tablet.core.ui.platform

import androidx.compose.runtime.Composable

// TODO: the fork reports the height through Keyboard.listenState { _, event -> event.height };
//  until that is wired up the modal stays where it is on Aurora.
@Composable
actual fun softKeyboardOverlapPx(): Int = 0
