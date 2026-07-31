package band.effective.office.tablet.core.ui.platform

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
