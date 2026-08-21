package band.effective.office.tablet.core.ui.common

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.cross
import band.effective.office.tablet.core.ui.res.vectorResource
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h6_button

@Composable
fun FreeBookingButtonView(
    modifier: Modifier,
    shape: RoundedCornerShape,
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean
) {
    Button(
        modifier = modifier,
        elevation = ButtonDefaults.elevatedButtonElevation(0.dp),
        colors = ButtonDefaults.buttonColors(Color.White),
        shape = shape,
        onClick = {
            onClick()
        }
    ) {
        Box(contentAlignment = Alignment.Center)
        {
            if (isLoading)
                Loader(elementColor = MaterialTheme.colorScheme.onSecondary)
            else
                Row {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.h6_button,
                        color = LocalCustomColorsPalette.current.busyStatus
                    )
                    Spacer(modifier = Modifier.size(15.dp))
                    Icon(
                        imageVector = vectorResource(Res.drawable.cross),
                        contentDescription = "Cross",
                        modifier = Modifier.size(20.dp),
                        tint = LocalCustomColorsPalette.current.busyStatus
                    )
                }

        }
    }
}