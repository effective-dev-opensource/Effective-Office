package band.effective.office.tablet.core.ui.platform

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

actual val forceLandscape: Boolean = true

actual val popupIsSeparateScene: Boolean = true

/**
 * Parity with the reference Android tablet, and it happens to be exact.
 *
 * Both devices are 1200 px on the short side — the Quadro T's window is 1200x2000 and the Android
 * reference is 1920x1200 — so a baseline of 686 dp substitutes a density of 1200/686 = 1.7493
 * against Android's own 1.75. The two lay out the same, not merely close.
 */
actual val uiScaleBaseline: Dp = 686.dp
