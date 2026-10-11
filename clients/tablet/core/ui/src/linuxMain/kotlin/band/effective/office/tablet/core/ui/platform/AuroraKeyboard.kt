package band.effective.office.tablet.core.ui.platform

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalDensity
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import ru.auroraos.kmp.window.Window
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.TimeSource

private const val KEYBOARD_TAG = "SoftKeyboard"

/** Height of maliit's key strip on a roomy panel, used when the inset cannot be believed. */
private const val KEYBOARD_STRIP_PX = 520

/** The most of the window's short side the strip takes, which is what a phone leaves it. */
private const val KEYBOARD_STRIP_MAX_FRACTION = 0.65f

/** How long after a press an inset is not yet proof, so a stale one cannot spend the promise. */
private val KEYBOARD_NOTICE_SETTLE = 300.milliseconds

/** What a press claims on its own, ahead of any inset, and when it claimed it. */
private class KeyboardPromise(
    val overlapPx: Int,
    val madeAt: TimeSource.Monotonic.ValueTimeMark,
)

private val keyboardPromise = mutableStateOf<KeyboardPromise?>(null)

/** Short side of the window, kept from the last composition so the press need not ask for it. */
private var lastShortSidePx = 0

private fun estimatedOverlapPx(shortSidePx: Int): Int =
    minOf(KEYBOARD_STRIP_PX, (shortSidePx * KEYBOARD_STRIP_MAX_FRACTION).roundToInt())

internal fun noteAuroraKeyboardExpected() {
    val shortSide = lastShortSidePx
    if (shortSide > 0) {
        keyboardPromise.value = KeyboardPromise(
            overlapPx = estimatedOverlapPx(shortSide),
            madeAt = TimeSource.Monotonic.markNow(),
        )
    }
}

/**
 * Reads the overlap off the fork's ime inset and decides whether the number means anything: one
 * reaching the window's short side is maliit's surface, not its key strip. See "The on-screen
 * keyboard" in clients/tablet/core/ui/README.md.
 */
@Composable
internal fun auroraKeyboardOverlapPx(): Int {
    val promise by keyboardPromise
    val density = LocalDensity.current
    // The fork keeps the inset in the scene's own density, and this runs under a substituted one.
    val inset = WindowInsets.ime.getBottom(density)
    val reported = (inset * Window.scale() / density.density).roundToInt()
    val shortSide = minOf(Window.width(), Window.height())
    val believable = reported in 1 until shortSide
    val overlap = when {
        reported <= 0 -> 0
        believable -> reported
        else -> estimatedOverlapPx(shortSide)
    }

    SideEffect {
        if (shortSide > 0) lastShortSidePx = shortSide
    }
    LaunchedEffect(reported) {
        Napier.i(tag = KEYBOARD_TAG) {
            "overlap ${overlap}px (inset ${reported}px, short side ${shortSide}px" +
                (if (reported > 0 && !believable) ", estimated)" else ")")
        }
    }
    LaunchedEffect(promise, reported > 0) {
        val kept = promise ?: return@LaunchedEffect
        if (reported > 0) {
            // The keyboard is really up, so the promise has done its job — unless the inset is one
            // the last edit left behind, which the settle window is there to outlast.
            delay(KEYBOARD_NOTICE_SETTLE - kept.madeAt.elapsedNow())
        } else {
            // Nothing came: the card should not stay lifted on a stale hope.
            delay(SOFT_KEYBOARD_PRESS_GRACE - kept.madeAt.elapsedNow())
        }
        // A press in the meantime has made a promise of its own, which is not this one's to end.
        if (keyboardPromise.value === kept) keyboardPromise.value = null
    }
    // The promise wins only while it is larger: once the inset answers, that is the truth.
    return maxOf(overlap, promise?.overlapPx ?: 0)
}
