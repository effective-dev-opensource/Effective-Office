package band.effective.office.tablet.core.domain.platform

import kotlin.time.Duration

/**
 * How often to re-read the rooms from the server on this platform, or `null` to leave it to push.
 *
 * Only Android has push: `Collector.emit` is called from exactly one place, the Firebase messaging
 * service in `androidMain`. iOS and Aurora therefore have to poll, or a booking made from a laptop
 * never reaches them at all.
 *
 * Android polls too at the moment, which is **temporary** — see the comment on its actual. Push
 * makes polling unnecessary there, but only for as long as the push actually arrives; when it does
 * not, nothing else notices, and the screen stays wrong until the app is restarted.
 */
expect val roomRefreshInterval: Duration?
