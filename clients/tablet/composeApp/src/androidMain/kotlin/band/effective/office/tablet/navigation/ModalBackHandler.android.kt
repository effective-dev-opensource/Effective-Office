package band.effective.office.tablet.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable

@Composable
actual fun ModalBackHandler(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
}
