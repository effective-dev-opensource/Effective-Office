package band.effective.office.elevator.components.bottomSheet

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.runtime.Composable
import band.effective.office.elevator.ui.booking.models.BottomSheetNames
import band.effective.office.elevator.utils.Stack
import band.effective.office.elevator.utils.peek
import band.effective.office.elevator.utils.pop
import band.effective.office.elevator.utils.push
import band.effective.office.elevator.utils.stackOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class MultiBottomSheetController(
    private val sheetStack: Stack<String> = stackOf(),
    private val sheetContents: Map<String, BottomSheetItem>
) {
    val currentState: MutableStateFlow<ModalBottomSheetState> =
        MutableStateFlow(getCurrentSheetState())

    suspend fun showSheet(nameSheet: String) {
        //closeCurrentSheet()
        sheetStack.push(nameSheet)
        currentState.update { getCurrentSheetState() }
        sheetContents[nameSheet]?.showBottomSheet()
    }

    suspend fun closeCurrentSheet() {
        sheetContents[sheetStack.pop()]?.hideBottomSheet()
        currentState.update { getCurrentSheetState()}
    }

    fun getCurrentSheetState(): ModalBottomSheetState {
        println(sheetStack.size)
        return sheetContents[sheetStack.peek()]?.bottomSheetContentState
            ?: sheetContents[BottomSheetNames.BOOK_REPEAT.name]?.bottomSheetContentState!!
    }

    fun getCurrentSheetContent(): @Composable ColumnScope.() -> Unit {
        return sheetContents[sheetStack.peek()]?.bottomSheetContent ?: {}
    }

    suspend fun showOver(nameSheet: String) {
        currentState.update { getCurrentSheetState() }
        sheetStack.push(nameSheet)
        sheetContents[nameSheet]?.bottomSheetContentState?.show()
    }
}