package band.effective.office.tablet.platform

import androidx.compose.ui.unit.Dp

/**
 * Padding for Aurora's status bar. Applied INSIDE the rotated content (see AppRoot), so in
 * landscape it ends up on top rather than down the side. Zero on Android and iOS, where
 * `systemBarsPadding()` already covers the system bars.
 */
expect val statusBarInset: Dp
