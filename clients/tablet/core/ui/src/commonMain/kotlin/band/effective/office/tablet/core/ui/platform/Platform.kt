package band.effective.office.tablet.core.ui.platform

import androidx.compose.ui.unit.Dp

/**
 * The tablet is a landscape-locked kiosk, but the Aurora window has no orientation handling and
 * on a portrait screen the whole UI — laid out horizontally — gets squashed. On Aurora we force
 * landscape by rotating the content; on Android and iOS orientation is left to the system.
 *
 * The rotation itself only happens when the window really is portrait (see [ForcedLandscape]),
 * so the flag breaks nothing on a landscape screen.
 */
expect val forceLandscape: Boolean

/**
 * Whether the platform renders a `Popup` as a scene of its own.
 *
 * The Aurora fork does: a popup gets its own scene in the untouched window — unrotated and with
 * the system density — so nothing applied at the root reaches it, its position provider cannot
 * anchor against the content layout, and the layer has to be stretched and positioned by hand.
 * Android and iOS render a popup in the same scene, where the ordinary anchored positioning works.
 *
 * This is deliberately not [forceLandscape]: the popup problem is scene isolation, and rotation is
 * only one of its consequences.
 */
expect val popupIsSeparateScene: Boolean

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
