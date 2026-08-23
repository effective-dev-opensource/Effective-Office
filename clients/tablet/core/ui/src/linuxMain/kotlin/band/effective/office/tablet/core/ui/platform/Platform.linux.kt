package band.effective.office.tablet.core.ui.platform

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
