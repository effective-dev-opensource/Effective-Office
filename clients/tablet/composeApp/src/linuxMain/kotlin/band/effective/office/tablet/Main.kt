package band.effective.office.tablet

import androidx.compose.ui.window.application
import band.effective.office.tablet.core.domain.model.SettingsManager
import band.effective.office.tablet.core.ui.platform.AuroraKeyboardSessionCloser
import band.effective.office.tablet.di.KoinInitializer
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

private const val STARTUP_TAG = "Startup"

// The fork opens the window itself, so there is neither Window nor singleWindowApplication here,
// and the root LocalViewModelStoreOwner comes from AppRoot: Aurora has no Activity or
// ViewController to provide one. There are no Firebase topics either.
fun main() {
    // LoggerInitializer gates the Antilog on isDebug, and the Aurora variant links a release binary.
    Napier.base(DebugAntilog())
    Napier.i(tag = STARTUP_TAG) { "meeting room tablet v${BuildKonfig.VERSION_NAME} started" }
    KoinInitializer().init()
    SettingsManager.init(AuroraSettingsStore())

    application {
        AuroraKeyboardSessionCloser()
        AppRoot()
    }
}
