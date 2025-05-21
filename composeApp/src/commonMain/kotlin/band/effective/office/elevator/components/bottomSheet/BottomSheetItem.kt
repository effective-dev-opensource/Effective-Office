package band.effective.office.elevator.components.bottomSheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable

class BottomSheetItem(
    val bottomSheetContentState: ModalBottomSheetState,
    val bottomSheetContent: @Composable ColumnScope.() -> Unit
) {
    suspend fun hideBottomSheet() = bottomSheetContentState.hide()

    suspend fun showBottomSheet() = bottomSheetContentState.show()
}
