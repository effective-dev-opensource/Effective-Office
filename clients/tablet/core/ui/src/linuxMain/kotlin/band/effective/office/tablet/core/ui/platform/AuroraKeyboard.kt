package band.effective.office.tablet.core.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import io.github.aakira.napier.Napier
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
