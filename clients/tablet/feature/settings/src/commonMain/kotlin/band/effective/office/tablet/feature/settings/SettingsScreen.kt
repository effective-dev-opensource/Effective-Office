package band.effective.office.tablet.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.LoadMainScreen
import band.effective.office.tablet.feature.settings.components.ChooseButtonView
import band.effective.office.tablet.feature.settings.components.ExitButtonView
import band.effective.office.tablet.feature.settings.components.GridRooms
import band.effective.office.tablet.feature.settings.components.TitleView

@Composable
fun SettingsScreen(component: SettingsComponent) {
    val state by component.state.collectAsState()

    if (state.loading) {
        LoadMainScreen()
    } else if (state.error.isNotEmpty()) {
        Text(
            text = state.error,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = 30.dp, vertical = 20.dp)
        )
    } else {
        SettingsView(
            listRooms = state.rooms,
            nameCurrentRoom = state.currentName,
            onExitApp = { component.sendIntent(Intent.OnExitApp) },
            onChangeCurrentName = { name: String ->
                component.sendIntent(Intent.ChangeCurrentNameRoom(name))
            },
            onSaveData = {
                component.sendIntent(Intent.SaveData)
            }
        )
    }
}

@Composable
fun SettingsView(
    listRooms: List<String>,
    nameCurrentRoom: String,
    onExitApp: () -> Unit,
    onChangeCurrentName: (name: String) -> Unit,
    onSaveData: () -> Unit
) {
    val padding = 35.dp
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),

        ) {
        ExitButtonView(
            modifier = Modifier
                .fillMaxHeight(0.15f)
                .padding(start = padding),
            onExitApp = onExitApp
        )
        TitleView(modifier = Modifier.padding(start = padding))
        Spacer(modifier = Modifier.height(15.dp))
        GridRooms(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = padding)
                .selectableGroup(),
            data = listRooms,
            currentName = nameCurrentRoom,
            onChangeCurrentName = onChangeCurrentName
        )
        Spacer(modifier = Modifier.height(80.dp))
        if (nameCurrentRoom.isNotEmpty()) {
            ChooseButtonView(
                modifier = Modifier.fillMaxWidth(),
                nameRoom = nameCurrentRoom,
                onSaveData = onSaveData
            )
        }
    }
}