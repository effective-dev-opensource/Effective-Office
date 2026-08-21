package band.effective.office.tablet.feature.main.components.uiComponent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.ethernet
import band.effective.office.tablet.core.ui.quantity
import band.effective.office.tablet.core.ui.res.painterResource
import band.effective.office.tablet.core.ui.theme.h1
import band.effective.office.tablet.core.ui.theme.h8
import band.effective.office.tablet.core.ui.theme.roomInfoColor
import band.effective.office.tablet.core.ui.theme.undefineStateColor
import band.effective.office.tablet.core.ui.tv
import band.effective.office.tablet.feature.main.tv_property
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun CommonRoomInfoComponent(
    modifier: Modifier = Modifier,
    name: String,
    capacity: Int,
    isHaveTv: Boolean,
    electricSocketCount: Int,
    backgroundColor: Color,
    isError: Boolean,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .padding(20.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(color = if (isError) undefineStateColor else backgroundColor)
            .fillMaxWidth()
    ) {
        Column(modifier) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                val spaceBetweenProperty = 20.dp
                Text(
                    text = name,
                    style = MaterialTheme.typography.h1,
                    color = roomInfoColor
                )
                Spacer(modifier = Modifier.width(spaceBetweenProperty))
                RoomProperty(
                    spaceBetweenProperty = spaceBetweenProperty,
                    capacity = capacity,
                    isHaveTv = isHaveTv,
                    electricSocketCount = electricSocketCount
                )
            }
            content()
        }
    }
}

@Composable
fun RoomProperty(
    spaceBetweenProperty: Dp,
    capacity: Int,
    isHaveTv: Boolean,
    electricSocketCount: Int
) {
    Row {
        if (isHaveTv) {
            RoomPropertyComponent(
                image = Res.drawable.tv,
                text = "",
                color = roomInfoColor
            )
            Spacer(modifier = Modifier.width(spaceBetweenProperty))
        }
        RoomPropertyComponent(
            image = Res.drawable.quantity,
            text = "$capacity",
            color = roomInfoColor,
            textModifier = Modifier.widthIn(min = 20.dp)
        )
        if (electricSocketCount > 0) {
            Spacer(modifier = Modifier.width(spaceBetweenProperty))
            RoomPropertyComponent(
                image = Res.drawable.ethernet,
                text = "$electricSocketCount",
                color = roomInfoColor
            )
        }
    }
}

@Composable
fun RoomPropertyComponent(
    image: DrawableResource,
    text: String,
    color: Color,
    textModifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier,
            painter = painterResource(image),
            contentDescription = null,
            colorFilter = ColorFilter.tint(color)
        )
        if (text.isNotEmpty()) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = text, style = MaterialTheme.typography.h8, modifier = textModifier)
        }
    }
}