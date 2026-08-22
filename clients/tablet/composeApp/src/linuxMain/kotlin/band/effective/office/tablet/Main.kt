package band.effective.office.tablet

import androidx.compose.ui.window.application
import band.effective.office.tablet.core.domain.model.SettingsManager
import band.effective.office.tablet.di.KoinInitializer

// The fork opens the window itself, so there is neither Window nor singleWindowApplication here,
// and the root LocalViewModelStoreOwner comes from AppRoot: Aurora has no Activity or
// ViewController to provide one. There are no Firebase topics either.
fun main() {
    LoggerInitializer().init()
    KoinInitializer().init()
    SettingsManager.init(AuroraSettingsStore())

    application {
        AppRoot()
    }
}
