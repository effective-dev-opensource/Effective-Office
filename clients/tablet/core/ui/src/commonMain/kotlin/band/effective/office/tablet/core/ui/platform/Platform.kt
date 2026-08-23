package band.effective.office.tablet.core.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp

expect fun getCurrentLanguageCode(): String

/**
 * Whether the content has to be turned landscape by hand. The tablet is a landscape-locked kiosk
 * and the Aurora window arrives portrait; Android and iOS leave orientation to the system.
 */
expect val forceLandscape: Boolean

/**
 * Padding for Aurora's status bar, applied by [AuroraWindowFrame] inside the rotation. Zero on
 * Android and iOS, where `systemBarsPadding()` covers the system bars instead.
 */
expect val statusBarInset: Dp

/**
 * Re-applies around a scene of its own what [AuroraWindowFrame] applies around the root: the fork
 * draws a `Dialog` as a separate scene in the untouched window, which inherits none of it.
 */
@Composable
expect fun DialogSceneFrame(content: @Composable () -> Unit)
