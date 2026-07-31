package band.effective.office.tablet.core.domain.platform

import kotlin.time.Duration

/**
 * How often to re-read the rooms from the server on this platform, or `null` to leave it to push.
 *
 * Android and iOS learn about bookings made elsewhere over FCM, which is immediate and costs
 * nothing while nothing changes — they poll not at all. Aurora has no FCM and no substitute, so
 * without polling a booking made from a laptop never reaches the tablet at all.
 */
expect val roomRefreshInterval: Duration?
