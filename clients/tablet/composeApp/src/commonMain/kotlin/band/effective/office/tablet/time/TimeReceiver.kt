package band.effective.office.tablet.time

/**
 * Drives [band.effective.office.tablet.feature.main.domain.CurrentTimeHolder]: the header clock and
 * the "N minutes left" countdown on the room card have to move, and something has to move them.
 *
 * One implementation per platform, on purpose. A common coroutine timer would work everywhere and
 * cost more everywhere: each system already has a way of waking an app once a minute, and using it
 * is cheaper than holding a timer of our own — this thing runs for the whole life of a kiosk that
 * is expected to sit on a wall for days. Android has the system tick broadcast and needs no timer
 * at all; iOS has NSTimer on the run loop; Aurora has neither, so there the coroutine is not a
 * choice but the only option left.
 *
 * The instance comes from Koin (`timeReceiverModule()`), because the platform that needs a `Context`
 * is exactly the platform whose graph has one. It is started and stopped by `AppRoot`, the single
 * root all three platforms share — the previous version was constructed in `AppActivity` alone,
 * which is why the clock never moved on iOS or Aurora.
 *
 * Every implementation flips the displayed time at :00 rather than a minute after launch.
 */
expect class TimeReceiver {

    /** Begins delivering time updates. Calling it twice is a no-op. */
    fun start()

    /** Stops delivering them and releases whatever [start] took. Calling it twice is a no-op. */
    fun stop()
}
