package band.effective.office.tablet.core.domain.platform

import kotlin.time.Duration

/**
 * How often to re-read the rooms from the server on this platform, or `null` to leave it to push.
 *
 * Only Android has push: `Collector.emit` is called from exactly one place, the Firebase messaging
 * service in `androidMain`. iOS and Aurora therefore have to poll, or a booking made from a laptop
 * never reaches them at all.
 *
 * Android stays on push alone and polls not at all. Worth knowing what that costs: if a push fails
 * to arrive, nothing else notices — the room list is served from the cache, and the cache is only
 * refreshed by a push — so the screen stays wrong until the app is restarted. A low-frequency
 * backstop here would close that off.
 */
expect val roomRefreshInterval: Duration?
