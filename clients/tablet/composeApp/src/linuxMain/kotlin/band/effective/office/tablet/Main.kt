package band.effective.office.tablet

import androidx.compose.ui.window.application
import band.effective.office.tablet.core.domain.model.SettingsManager
import band.effective.office.tablet.core.ui.platform.AURORA_LOCALE
import band.effective.office.tablet.core.ui.platform.AuroraKeyboardSessionCloser
import band.effective.office.tablet.di.KoinInitializer
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import platform.posix.setenv

private const val STARTUP_TAG = "Startup"
private const val LOCALE_ENV_KEY = "LC_ALL"
private const val OVERWRITE_ENV = 1

// The fork opens the window itself, so there is neither Window nor singleWindowApplication here,
// and the root LocalViewModelStoreOwner comes from AppRoot: Aurora has no Activity or
// ViewController to provide one. There are no Firebase topics either.
fun main() {
    // The resource language is read off the process environment, and the seam that would override
    // it is internal — see "Resource language on Aurora" in clients/tablet/composeApp/README.md.
    setenv(LOCALE_ENV_KEY, AURORA_LOCALE, OVERWRITE_ENV)
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
