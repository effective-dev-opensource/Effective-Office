package band.effective.office.elevator.ui.bottomSheets.bookPeriodSheet.ChooseZoneSheet

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import band.effective.office.elevator.ui.booking.components.modals.ChooseZone
import band.effective.office.elevator.ui.booking.models.WorkspaceZoneUI
import band.effective.office.elevator.ui.bottomSheets.BottomSheet
import band.effective.office.elevator.ui.bottomSheets.bookPeriodSheet.ChooseZoneSheet.store.ChooseZoneStore
import band.effective.office.elevator.ui.bottomSheets.bookPeriodSheet.ChooseZoneSheet.store.ChooseZoneStoreFactory
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.extensions.coroutines.stateFlow
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.stringResource

class ChooseZoneSheetComponent(
    private val sheetTile: StringResource,
    workSpacesZone: List<WorkspaceZoneUI>,
    closeSheet: () -> Unit,
    confirm: (List<WorkspaceZoneUI>) -> Unit
) : BottomSheet {
    val store = ChooseZoneStoreFactory(
        storeFactory = DefaultStoreFactory(),
        close = closeSheet,
        confirm = confirm
    ).create(workSpacesZone)

    @Composable
    override fun SheetContent() {
        val state by store.stateFlow.collectAsState()
        ChooseZone(
            sheetTile = stringResource(sheetTile),
            workSpacecZone = state.zones,
            onClickCloseChoseZone = { store.accept(ChooseZoneStore.Intent.onCloseRequest) },
            onClickConfirmSelectedZone = { store.accept(ChooseZoneStore.Intent.onConfirmRequest) },
            onClickZone = { store.accept(ChooseZoneStore.Intent.onZoneClick(it)) }
        )
    }
}