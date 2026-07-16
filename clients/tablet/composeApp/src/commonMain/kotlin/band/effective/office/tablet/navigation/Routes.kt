package band.effective.office.tablet.navigation

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import kotlinx.serialization.Serializable

/**
 * Type-safe compose-navigation routes for the tablet app.
 *
 * Full-screen destinations are hosted with `composable<Route>`; modal windows (and the date/time
 * picker) with `dialog<Route>` — they are full-fledged navigation destinations, each its own dialog
 * window, rather than state-driven overlays.
 */

/** Settings screen — start destination when no room is configured yet. */
@Serializable
object SettingsRoute

/** Main room list / dashboard — start destination once settings exist. */
@Serializable
object MainRoute

/** Modal: release ("free up") the current room's event. */
@Serializable
data class FreeRoomRoute(val event: EventInfo, val roomName: String)

/**
 * Nested navigation graph for the booking-editor flow. Carries the runtime payload so the graph
 * entry can own the shared `BookingEditorViewModel`; both the editor and the date/time picker
 * resolve that same ViewModel from this graph entry (see AppNavHost), so the picked date flows back
 * through shared state without nav-result plumbing.
 */
@Serializable
data class BookingFlowRoute(val event: EventInfo, val room: String)

/** Modal: create/edit a booking — start destination of [BookingFlowRoute]. */
@Serializable
object BookingEditorRoute

/** Modal: quick booking of the nearest available room. */
@Serializable
data class FastBookingRoute(
    val minEventDuration: Int,
    val selectedRoom: RoomInfo,
    val rooms: List<RoomInfo>,
)

/**
 * Date/time picker for the booking editor. Carries no payload: it lives inside [BookingFlowRoute]
 * and shares the graph entry's `BookingEditorViewModel` (and its `dateTimePickerComponent`), so the
 * picked date flows back through the shared state.
 */
@Serializable
object DateTimePickerRoute
