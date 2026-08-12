package band.effective.office.tablet.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe compose-navigation routes for the tablet app.
 *
 * Only the two full-screen destinations are routes, hosted with `composable<Route>`. The modals and
 * the date/time picker are not navigation destinations at all — they are state-driven overlays in
 * the main composition (see AppNavHost), because calf's native iOS pickers do not receive touches
 * inside a Compose dialog window.
 */

/** Settings screen — start destination when no room is configured yet. */
@Serializable
object SettingsRoute

/** Main room list / dashboard — start destination once settings exist. */
@Serializable
object MainRoute
