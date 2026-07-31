package band.effective.office.tablet

import androidx.compose.ui.window.application
import band.effective.office.tablet.di.KoinInitializer
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun main() {
    Napier.base(DebugAntilog())
    KoinInitializer().init()

    application {
        AppRoot()
    }
}
