package band.effective.office.tablet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import io.github.aakira.napier.Napier
import ru.auroraos.kmp.window.WindowEvents
import ru.auroraos.kmp.window.events.LifecycleEvent

/**
 * Works around the fork's input teardown on window Pause.
 *
 * The fork's ComposeWindow maps LifecycleEvent.Pause to a teardown of its input and
 * touch listeners and re-arms them on Resume. Hiding the maliit keyboard produces a
 * Pause with no matching Resume (the compositor's focus thrash also logs
 * "MAttributeExtensionManager ... Invalid focus state"), which leaves the app deaf to
 * all touch and key input while it keeps rendering — a permanent freeze from the
 * user's point of view.
 *
 * The listeners are private to the fork, so they cannot be re-armed from the app.
 * What the app can do is stop the Pause from ever reaching the fork: once the first
 * Resume has been dispatched — at which point the fork has armed its input and touch
 * listeners — every lifecycle subscription is dropped via the public
 * [WindowEvents.unlistenLifecycleAll]. With nobody listening, Pause never fires the
 * teardown and the listeners armed by that first Resume stay armed for the lifetime
 * of the process.
 *
 * The cost: the Compose Lifecycle is frozen in RESUMED and the fork's
 * Keyboard.dispose() on Pause no longer runs. For a wall-mounted kiosk that never
 * backgrounds, neither matters — and skipping the dispose also avoids the maliit
 * input-context leak that provokes the focus thrash in the first place.
 *
 * Everything here runs on the single native event-loop thread (callbacks and the
 * Compose UI dispatcher alike), so the check-then-unlisten below cannot interleave
 * with an incoming Pause.
 */
private var lastLifecycleEvent: LifecycleEvent? = null

/** Call from main() before application {} so the watcher registers ahead of the fork's listener. */
fun installFreezeGuard() {
    WindowEvents.listenLifecycle { _, event ->
        lastLifecycleEvent = event
    }
}

@Composable
fun AuroraFreezeGuard() {
    LaunchedEffect(Unit) {
        while (lastLifecycleEvent != LifecycleEvent.Resume) {
            withFrameNanos { }
        }
        WindowEvents.unlistenLifecycleAll()
        Napier.i(tag = "FreezeGuard") { "lifecycle listeners dropped after first Resume; Pause can no longer tear down input" }
    }
}
