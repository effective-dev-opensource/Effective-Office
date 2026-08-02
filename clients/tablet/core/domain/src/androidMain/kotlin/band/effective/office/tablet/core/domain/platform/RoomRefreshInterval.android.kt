package band.effective.office.tablet.core.domain.platform

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

// TEMPORARY — to be reverted to null.
//
// Android has real push in production, so it does not need to poll. This is a backstop put in
// while the Aurora work is being tested: when a push does not arrive, Android goes stale silently
// and stays that way until the app is restarted, which on a wall-mounted tablet can be weeks. It
// was caught exactly that way — the emulator kept showing a slot as free for an hour after another
// client had booked it.
//
// Revert this to `null` once push delivery has been confirmed end to end, or replace it with a
// deliberately low-frequency backstop rather than a full minute.
actual val roomRefreshInterval: Duration? = 1.minutes
