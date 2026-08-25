package band.effective.office.tablet.core.ui.platform

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.lazy.LazyListState

actual fun getCurrentLanguageCode(): String = NSLocale.currentLocale.languageCode ?: "en"

actual val forceLandscape: Boolean = false

actual val statusBarInset: Dp = 0.dp

actual val uiScaleBaseline: Dp = 0.dp

@Composable
actual fun DialogSceneFrame(content: @Composable () -> Unit) = content()

// UIKit shortens the scene to the area above the keyboard, so nothing that is left is covered.
@Composable
actual fun softKeyboardOverlapPx(): Int = 0

actual fun noteSoftKeyboardExpected() = Unit

// UIKit takes the keyboard down with the focus by itself.
actual fun closeSoftKeyboard() = Unit


@Composable
actual fun listFlingBehavior(): FlingBehavior = ScrollableDefaults.flingBehavior()

@Composable
actual fun snapListFlingBehavior(state: LazyListState): FlingBehavior =
    rememberSnapFlingBehavior(state)
