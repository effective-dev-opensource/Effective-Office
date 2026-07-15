import androidx.compose.ui.window.ComposeUIViewController
import band.effective.office.tablet.AppRoot
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController =
    ComposeUIViewController {
        AppRoot()
    }
