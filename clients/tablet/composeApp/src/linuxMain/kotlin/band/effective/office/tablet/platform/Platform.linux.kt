package band.effective.office.tablet.platform

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

// Aurora's status bar sits on top; 24dp is enough and costs little space.
actual val statusBarInset: Dp = 24.dp

// Not isDebug: the Aurora variant links a release binary, so that flag is false here. The overlay
// stays until the scale baseline has been confirmed on the device.
actual val showDiagnosticsOverlay: Boolean = true
