package band.effective.office.tablet.core.ui.platform

/**
 * The tablet is a landscape-locked kiosk, but the Aurora window has no orientation handling and
 * on a portrait screen the whole UI — laid out horizontally — gets squashed. On Aurora we force
 * landscape by rotating the content; on Android and iOS orientation is left to the system.
 *
 * The rotation itself only happens when the window really is portrait (see [ForcedLandscape]),
 * so the flag breaks nothing on a landscape screen.
 */
expect val forceLandscape: Boolean
