package band.effective.office.tablet.platform

import androidx.compose.ui.unit.Dp

/**
 * Padding for Aurora's status bar. Applied INSIDE the rotated content (see AppRoot), so in
 * landscape it ends up on top rather than down the side. Zero on Android and iOS, where
 * `systemBarsPadding()` already covers the system bars.
 */
expect val statusBarInset: Dp

/**
 * Whether to draw the version and screen-metrics line in the corner.
 *
 * On Android and iOS this follows the debug flag — it is a diagnostic, and until now it was drawn
 * unconditionally, which put it in release builds of both.
 *
 * On Aurora it is always on, and deliberately so: the Aurora variant links a *release* binary, so
 * `isDebug` is false there, and the overlay is still the only way to read the window size and the
 * density that `ScaledUiDensity` computed. It comes out once the scale baseline has been confirmed
 * on the device.
 */
expect val showDiagnosticsOverlay: Boolean
