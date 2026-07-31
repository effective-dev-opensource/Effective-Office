package band.effective.office.tablet.platform

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
