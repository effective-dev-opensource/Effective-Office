import androidx.compose.ui.uikit.OnFocusBehavior
import androidx.compose.ui.window.ComposeUIViewController
import band.effective.office.tablet.AppRoot
import band.effective.office.tablet.core.ui.theme.AppTheme
import platform.UIKit.UIViewController

/**
 * [OnFocusBehavior.DoNothing] instead of the default scroll-to-focused-element: `ModalHost` already
 * moves the focused field clear of the keyboard itself — a draw-time translation of the card, aimed
 * at the field rather than the card — and letting UIKit shove the content up on top of that pushed
 * the dialog's own title off the screen.
 */
fun MainViewController(): UIViewController =
    ComposeUIViewController(
        configure = { onFocusBehavior = OnFocusBehavior.DoNothing },
    ) {
        AppTheme {
            AppRoot()
        }
    }
