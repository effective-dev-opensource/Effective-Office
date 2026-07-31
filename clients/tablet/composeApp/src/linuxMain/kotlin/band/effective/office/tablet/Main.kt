package band.effective.office.tablet

import androidx.compose.ui.window.application
import band.effective.office.tablet.di.KoinInitializer

/**
 * Aurora entry point.
 *
 * Окно создаёт сам форк, поэтому ни Window, ни singleWindowApplication здесь нет.
 * Корневой LocalViewModelStoreOwner даёт сам AppRoot() — на Авроре его, в отличие от
 * Android (Activity) и iOS (ViewController), предоставить больше некому.
 * Firebase-топиков тут нет: пуш-обновлений комнат на Авроре не будет.
 */
fun main() {
    LoggerInitializer().init()
    KoinInitializer().init()

    application {
        AppRoot()
    }
}
