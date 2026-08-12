package band.effective.office.tablet

import androidx.compose.ui.window.application
import band.effective.office.tablet.core.ui.platform.AuroraKeyboardSessionCloser
import band.effective.office.tablet.core.ui.platform.AuroraWindowFrame
import band.effective.office.tablet.core.ui.theme.AppTheme
import band.effective.office.tablet.di.KoinInitializer
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun main() {
    Napier.base(DebugAntilog())
    KoinInitializer().init()

    application {
        AuroraKeyboardSessionCloser()
        // The theme goes outside the frame so its Surface paints the whole window, the strip
        // under the status bar included; the frame then pads the content away from that strip.
        // The frame itself is the one place the Aurora window is dealt with — rotation, inset,
        // dp space — and Android and iOS start AppRoot without it, because all three layers are
        // no-ops there anyway.
        AppTheme {
            AuroraWindowFrame {
                AppRoot()
            }
        }
    }
}
