package band.effective.office.tablet.core.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LayoutCoordinates

@Composable
internal actual fun OrganizerListPopup(
    textFieldCoords: LayoutCoordinates?,
    content: @Composable (Modifier) -> Unit,
) {
    AnchoredOrganizerList(textFieldCoords = textFieldCoords, content = content)
}
