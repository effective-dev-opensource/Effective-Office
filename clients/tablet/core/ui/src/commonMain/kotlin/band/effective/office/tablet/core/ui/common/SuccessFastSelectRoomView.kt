package band.effective.office.tablet.core.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.booked_until
import band.effective.office.tablet.core.ui.cancel_book
import band.effective.office.tablet.core.ui.date.timeFormatter
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h5
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.format
import org.jetbrains.compose.resources.stringResource

@Composable
fun SuccessFastSelectRoomView(
    roomName: String,
    finishTime: LocalDateTime,
    close: () -> Unit,
    onFreeRoomRequest: (String) -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(3))
            .background(LocalCustomColorsPalette.current.elevationBackground)
            .padding(top = 35.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CrossButtonView(
            Modifier
                .fillMaxWidth()
                .padding(end = 42.dp),
            onDismissRequest = { close() }
        )
        Spacer(modifier = Modifier.height(2.dp))
        IconSuccess()
        Spacer(modifier = Modifier.height(24.dp))
        SuccessText(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp),
            nameRoom = roomName
        )
        Spacer(modifier = Modifier.height(50.dp))
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(Res.string.booked_until, finishTime.format(timeFormatter)),
                style = MaterialTheme.typography.h5,
                color = LocalCustomColorsPalette.current.primaryTextAndIcon
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Spacer(modifier = Modifier.height(30.dp))
        FreeBookingButtonView(
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .height(64.dp),
            shape = RoundedCornerShape(100),
            text = stringResource(Res.string.cancel_book),
            onClick = {
                onFreeRoomRequest(roomName)
            },
            isLoading = isLoading
        )
        Spacer(Modifier.height(30.dp))
    }
}