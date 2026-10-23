package band.effective.office.tablet.feature.main.presentation.freeuproom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.common.CrossButtonView
import band.effective.office.tablet.core.ui.common.Loader
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h4
import band.effective.office.tablet.core.ui.theme.h6
import band.effective.office.tablet.core.ui.theme.textButton
import band.effective.office.tablet.feature.main.Res
import band.effective.office.tablet.feature.main.free_select_room
import band.effective.office.tablet.feature.main.free_select_room_button
import band.effective.office.tablet.feature.main.try_again
import org.jetbrains.compose.resources.stringResource

@Composable
fun FreeSelectRoom(
    onClose: () -> Unit,
    viewModel: FreeSelectRoomViewModel,
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.closeEvents.collect { onClose() }
    }

    FreeSelectRoom(
        onCloseRequest = { viewModel.sendIntent(Intent.OnCloseWindowRequest) },
        onFreeRoomRequest = { viewModel.sendIntent(Intent.OnFreeSelectRequest) },
        isLoading = state.isLoad,
        isFail = !state.isSuccess
    )
}

@Composable
private fun FreeSelectRoom(
    onCloseRequest: () -> Unit,
    onFreeRoomRequest: () -> Unit,
    isLoading: Boolean,
    isFail: Boolean
) {
    val shape = RoundedCornerShape(50)

    val isPressed = remember { mutableStateOf(false) }
    val colorButton = if (isPressed.value)
        LocalCustomColorsPalette.current.pressedPrimaryButton else MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(5))
            .background(LocalCustomColorsPalette.current.elevationBackground),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        CrossButtonView(
            Modifier.width(518.dp).padding(end = 42.dp),
            onDismissRequest = {
                onCloseRequest()
            }
        )
        Spacer(modifier = Modifier.height(30.dp))
        Box(
            modifier = Modifier.padding(0.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = stringResource(Res.string.free_select_room),
                style = MaterialTheme.typography.h4,
                color = LocalCustomColorsPalette.current.primaryTextAndIcon
            )
        }
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            modifier = Modifier.size(290.dp, 64.dp),
            colors = ButtonDefaults.buttonColors(colorButton),
            shape = shape,
            onClick = {
                isPressed.value = !isPressed.value
                onFreeRoomRequest()
            }
        ) {
            Box(contentAlignment = Alignment.Center) {
                when {
                    isLoading -> Loader()
                    isFail -> Text(
                        text = stringResource(Res.string.try_again),
                        style = MaterialTheme.typography.h6,
                        color = textButton,
                    )

                    else -> Text(
                        text = stringResource(Res.string.free_select_room_button),
                        style = MaterialTheme.typography.h6,
                        color = textButton,
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}
