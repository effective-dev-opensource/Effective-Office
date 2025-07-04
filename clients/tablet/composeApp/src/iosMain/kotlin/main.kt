import androidx.compose.ui.window.ComposeUIViewController
import band.effective.office.tablet.App
import band.effective.office.tablet.root.RootComponent
import platform.UIKit.UIViewController

fun rootViewController(root: RootComponent): UIViewController =
    ComposeUIViewController {
        App(root)
    }
