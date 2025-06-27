import androidx.compose.ui.window.ComposeUIViewController
import band.effective.office.tablet.App
import band.effective.office.tablet.LoggerInitializer
import band.effective.office.tablet.di.KoinInitializer
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController {
    LoggerInitializer().init()
    KoinInitializer().init()
    App()
}
