package band.effective.office.tablet.core.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.failure
import band.effective.office.tablet.core.ui.failure_text
import band.effective.office.tablet.core.ui.res.painterResource
import band.effective.office.tablet.core.ui.select_other_room
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h2
import band.effective.office.tablet.core.ui.theme.h4
import org.jetbrains.compose.resources.stringResource

@Composable
fun FailureSelectRoomView(onDismissRequest: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.75f)
            .clip(RoundedCornerShape(3))
            .background(LocalCustomColorsPalette.current.elevationBackground)
            .padding(35.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CrossButtonView(
            Modifier.fillMaxWidth(),
            onDismissRequest = { onDismissRequest() }
        )
        Spacer(modifier = Modifier.height(20.dp))
        Image(
            painter = painterResource(Res.drawable.failure),
            contentDescription = null,
            colorFilter = ColorFilter.tint(LocalCustomColorsPalette.current.busyStatus)

        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(Res.string.failure_text),
            style = MaterialTheme.typography.h2,
            color = LocalCustomColorsPalette.current.primaryTextAndIcon,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(Res.string.select_other_room),
            style = MaterialTheme.typography.h4,
            minLines = 2,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(30.dp))
    }
}