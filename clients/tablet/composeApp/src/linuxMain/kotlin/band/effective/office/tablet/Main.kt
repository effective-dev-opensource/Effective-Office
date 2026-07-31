package band.effective.office.tablet

import androidx.compose.ui.window.application
import band.effective.office.tablet.di.KoinInitializer
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

/**
 * Aurora entry point.
 *
 * Окно создаёт сам форк, поэтому ни Window, ни singleWindowApplication здесь нет.
 * Корневой LocalViewModelStoreOwner даёт сам AppRoot() — на Авроре его, в отличие от
 * Android (Activity) и iOS (ViewController), предоставить больше некому.
 * Firebase-топиков тут нет: пуш-обновлений комнат на Авроре не будет.
 */
fun main() {
    // LoggerInitializer ставит Antilog только при isDebug, а аврорский вариант линкуется
    // как release-бинарь (Platform.isDebugBinary == false), так что ставим напрямую —
    // иначе на устройстве не будет ни одной строки лога.
    Napier.base(DebugAntilog())
    KoinInitializer().init()

    application {
        AppRoot()
    }
}
