package band.effective.office.tablet.core.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.on_main
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.stringResource

@Composable
fun SuccessSelectRoomView(
    roomName: String,
    organizerName: String,
    startTime: LocalDateTime,
    finishTime: LocalDateTime,
    close: () -> Unit
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
        DateTimeView(
            modifier = Modifier.fillMaxWidth(),
            startTime = startTime,
            finishTime = finishTime,
        )
        Spacer(modifier = Modifier.height(12.dp))
        OrganizerEventView(organizer = organizerName)
        Spacer(modifier = Modifier.height(30.dp))
        BookingButtonView(
            modifier = Modifier
                .fillMaxWidth(0.72f)
                .height(64.dp),
            shape = RoundedCornerShape(100),
            text = stringResource(Res.string.on_main),
            onClick = close,
            isLoading = false,
        )
        Spacer(Modifier.height(30.dp))
    }
}
