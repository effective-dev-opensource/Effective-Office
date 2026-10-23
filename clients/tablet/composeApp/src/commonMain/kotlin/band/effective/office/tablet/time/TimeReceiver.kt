package band.effective.office.tablet.time

/**
 * Moves [band.effective.office.tablet.feature.main.domain.CurrentTimeHolder] once a minute, each
 * platform on the wake-up its own system already provides rather than on a timer of ours — this
 * runs for days on a wall. [start] and [stop] are idempotent.
 */
expect class TimeReceiver {

    fun start()

    fun stop()
}
