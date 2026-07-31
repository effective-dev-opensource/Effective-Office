package band.effective.office.tablet.core.ui.platform

import androidx.compose.ui.unit.Dp

/**
 * The reference short side of the window, in dp, that [ScaledUiDensity] normalises the UI to.
 *
 * The Compose scene density cannot be set on Aurora: the fork creates the scene as
 * `ComposeScene(density = Density(ru.auroraos.kmp.window.contentScale.toFloat()))`, and
 * `contentScale` arrives from the system together with the window. So the dp space is fixed
 * here instead: 800 dp on the short side is a familiar 10" tablet (the reference Android tablet
 * reports exactly the same — 2560x1600 at density 2.0).
 *
 * On Android and iOS the system provides the density and scaling is off ([Dp] == 0).
 */
expect val uiScaleBaseline: Dp
