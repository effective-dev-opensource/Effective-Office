package band.effective.office.tablet.core.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.withFrameNanos
import io.github.aakira.napier.Napier
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import ru.auroraos.kmp.keyboard.maliit.Keyboard
import ru.auroraos.kmp.window.Window
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

private const val KEYBOARD_POLL_MS = 100L

private const val KEYBOARD_TAG = "SoftKeyboard"

/** Share of the screen's short side taken for the keyboard when its own answer cannot be believed. */
private const val KEYBOARD_COVER_FRACTION = 0.43f

/** How long after a press `isOpen()` is not yet proof, so a stale `true` cannot spend the promise. */
private val KEYBOARD_NOTICE_SETTLE = 300.milliseconds

private var keyboardExpectedAt: TimeSource.Monotonic.ValueTimeMark? = null

internal fun noteAuroraKeyboardExpected() {
    keyboardExpectedAt = TimeSource.Monotonic.markNow()
}

/** One poll's worth of what the keyboard and the screen say, kept together for the log line. */
private data class KeyboardReading(
    val overlap: Int,
    val reported: Int,
    val isOpen: Boolean,
    val screenWidth: Int,
    val screenHeight: Int,
    val estimated: Boolean,
)

/**
 * Asks the keyboard how tall it is and decides whether the answer means anything: a height reaching
 * the screen's short side is maliit's surface, not its key strip. See the README section named in
 * [auroraKeyboardOverlapPx].
 */
private fun readKeyboard(): KeyboardReading {
    val reported = Keyboard.height().toInt()
    val isOpen = Keyboard.isOpen()
    val screenWidth = Window.screenWidth()
    val screenHeight = Window.screenHeight()
    val shortSide = minOf(screenWidth, screenHeight)

    if (shortSide <= 0) {
        return KeyboardReading(
            overlap = maxOf(0, reported),
            reported = reported,
            isOpen = isOpen,
            screenWidth = screenWidth,
            screenHeight = screenHeight,
            estimated = false,
        )
    }

    val notice = keyboardExpectedAt
    val expected = notice?.let { it.elapsedNow() < SOFT_KEYBOARD_PRESS_GRACE } == true
    val believable = reported in 1 until shortSide

    // A kept promise is dropped, or closing the keyboard inside the grace window would lift the
    // card straight back up for the rest of it.
    if (notice != null && (believable || (isOpen && notice.elapsedNow() > KEYBOARD_NOTICE_SETTLE))) {
        keyboardExpectedAt = null
    }

    val overlap = when {
        believable -> reported
        isOpen || expected -> (shortSide * KEYBOARD_COVER_FRACTION).roundToInt()
        else -> 0
    }
    return KeyboardReading(
        overlap = overlap,
        reported = reported,
        isOpen = isOpen,
        screenWidth = screenWidth,
        screenHeight = screenHeight,
        estimated = !believable && overlap > 0,
    )
}

/**
 * Polls the maliit session for the height the fork reports no insets for. **Must not become a
 * subscription** — see "The on-screen keyboard" in clients/tablet/core/ui/README.md.
 *
 * Stays on the composition's dispatcher: the maliit binding is a Qt object, bound to the thread it
 * was made on.
 */
@Composable
internal fun auroraKeyboardOverlapPx(): Int {
    val overlap by produceState(0) {
        while (true) {
            val reading = runCatching { readKeyboard() }.getOrElse {
                Napier.e(throwable = it, tag = KEYBOARD_TAG) { "height poll failed, giving up" }
                return@produceState
            }
            if (reading.overlap != value) {
                Napier.i(tag = KEYBOARD_TAG) {
                    "overlap ${value}px -> ${reading.overlap}px (isOpen=${reading.isOpen}, " +
                        "reported=${reading.reported}px, " +
                        "screen ${reading.screenWidth} x ${reading.screenHeight}" +
                        (if (reading.estimated) ", estimated)" else ")")
                }
                value = reading.overlap
            }
            delay(KEYBOARD_POLL_MS)
        }
    }
    return overlap
}

/** Conflated: ten requests before the next frame mean one close, which is all a session needs. */
private val keyboardCloseRequests = Channel<Unit>(Channel.CONFLATED)

// Fork defect: the fork opens the maliit session on focus and parks in awaitCancellation() with no
// finally, so it is never closed. See "Fork defects" in clients/tablet/core/ui/README.md.
internal fun requestAuroraKeyboardClose() {
    // Editing is over, so nobody is waiting for a keyboard: a promise left to expire would lift the
    // modal over an empty screen.
    keyboardExpectedAt = null
    keyboardCloseRequests.trySend(Unit)
}

/**
 * Closes the maliit session a frame after it is asked for. Mount once at the root, ahead of any
 * content that edits text; the frame clock is the one scheduler on this fork that really defers.
 */
@Composable
fun AuroraKeyboardSessionCloser() {
    LaunchedEffect(Unit) {
        for (request in keyboardCloseRequests) {
            // trySend may resume this inline, still inside the key dispatch Keyboard.close() would
            // deadlock against; the frame await is what guarantees it runs outside.
            withFrameNanos { }
            runCatching {
                // Unconditionally: by the time editing ends isOpen() is already false, and the
                // session behind it is exactly what is never torn down.
                val visible = Keyboard.isOpen()
                Keyboard.close()
                Napier.i(tag = KEYBOARD_TAG) {
                    "closed the session, keyboard was ${if (visible) "up" else "already down"}"
                }
            }.onFailure {
                Napier.e(throwable = it, tag = KEYBOARD_TAG) { "closing the session failed" }
            }
        }
    }
}
