package band.effective.office.tablet.core.ui.platform

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollScope
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.lazy.LazyListState
import io.github.aakira.napier.Napier
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * POSIX name of the locale the app runs under, spelled the way Aurora spells it. `main` puts it
 * into the process environment — see "Resource language on Aurora" in
 * clients/tablet/composeApp/README.md.
 */
const val AURORA_LOCALE = "ru_RU.utf8"

actual fun getCurrentLanguageCode(): String = AURORA_LOCALE.substringBefore('_')

actual val forceLandscape: Boolean = true

actual val statusBarInset: Dp = 24.dp

actual val uiScaleBaseline: Dp = 686.dp

@Composable
actual fun DialogSceneFrame(content: @Composable () -> Unit) {
    AuroraWindowFrame { content() }
}

@Composable
actual fun softKeyboardOverlapPx(): Int = auroraKeyboardOverlapPx()

actual fun noteSoftKeyboardExpected() = noteAuroraKeyboardExpected()

actual fun closeSoftKeyboard() = requestAuroraKeyboardClose()

private const val FLING_TAG = "ListFling"

// Fork defect: the fling velocity arrives with the sign flipped.
// See "Fling direction" in clients/tablet/core/ui/README.md.
/** Wraps [delegate], flipping the sign of the velocity going in and the remainder coming out. */
@Composable
private fun invertedFling(delegate: FlingBehavior): FlingBehavior =
    androidx.compose.runtime.remember(delegate) {
        object : FlingBehavior {
            override suspend fun ScrollScope.performFling(initialVelocity: Float): Float {
                val left = with(delegate) { performFling(-initialVelocity) }
                Napier.i(tag = FLING_TAG) { "fling v=$initialVelocity flipped, unconsumed=$left" }
                return -left
            }
        }
    }

@Composable
actual fun listFlingBehavior(): FlingBehavior = invertedFling(ScrollableDefaults.flingBehavior())

@Composable
actual fun snapListFlingBehavior(state: LazyListState): FlingBehavior =
    invertedFling(rememberSnapFlingBehavior(state))
