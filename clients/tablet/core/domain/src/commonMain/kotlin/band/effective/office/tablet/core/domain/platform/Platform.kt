package band.effective.office.tablet.core.domain.platform

import kotlin.time.Duration

/**
 * How often to re-read the rooms from the server on this platform, or `null` to leave it to push.
 *
 * Only Android has push: `Collector.emit` is called from the Firebase messaging service in
 * `androidMain` and from nowhere else, so everyone else has to poll.
 */
expect val roomRefreshInterval: Duration?
