package band.effective.office.tablet.core.domain.platform

import kotlin.time.Duration

/**
 * How often to re-read the rooms from the server on this platform, or `null` to leave it to push.
 *
 * Only Android has push: `Collector.emit` is called from exactly one place, the Firebase messaging
 * service in `androidMain`. So Android leaves this `null` — a push is immediate and costs nothing
 * while nothing changes — and iOS and Aurora poll, because otherwise a booking made from a laptop
 * never reaches them at all.
 */
expect val roomRefreshInterval: Duration?
