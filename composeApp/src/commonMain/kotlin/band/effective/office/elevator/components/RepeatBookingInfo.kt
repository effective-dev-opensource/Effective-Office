package band.effective.office.elevator.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.unit.IntOffset
import band.effective.office.elevator.EffectiveTheme
import band.effective.office.elevator.MainRes
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun IconWithRepeatBookingInfo(
    modifier: Modifier = Modifier.size(24.dp),
    onClick: () -> Unit,
    showInfo: Boolean,
    onDismissRequest: () -> Unit,
    repeatDaysText: String,
    untilDateText: String
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        IconButton(onClick = onClick) {
            Icon(
                painter = painterResource(MainRes.images.ic_repeat),
                contentDescription = stringResource(MainRes.strings.repeat_booking)
            )
        }
        if (showInfo) {
            RepeatBookingPopup(
                onDismissRequest = onDismissRequest,
                content = {
                    RepeatBookingInfo(
                        repeatDaysText = repeatDaysText,
                        untilDateText = untilDateText
                    )
                }
            )
        }
    }
}

@Composable
fun RepeatBookingPopup(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
    onDismissRequest: () -> Unit
) {
    Popup(
        alignment = Alignment.TopCenter,
        offset = IntOffset(x = -64, y = 64),
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = modifier.wrapContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RepeatBookingTriangle()
            Box(
                modifier = Modifier
                    .background(
                        color = EffectiveTheme.colors.icon.primary,
                        shape = RoundedCornerShape(8.dp)
                    )
            ) {
                content()
            }
        }
    }
}

@Composable
fun RepeatBookingInfo(
    repeatDaysText: String,
    untilDateText: String
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = repeatDaysText,
            color = EffectiveTheme.colors.text.caption
        )
        Text(
            text = untilDateText,
            color = EffectiveTheme.colors.text.caption
        )
    }
}

@Composable
fun RepeatBookingTriangle(
    modifier: Modifier = Modifier.size(28.dp, 17.dp)
) {
    val iconColor = EffectiveTheme.colors.icon.primary
    Canvas(modifier = modifier.offset(x = 24.dp, y = 0.dp)) {
        val width = size.width
        val height = size.height

        fun px(x: Float): Float = x / 28f * width
        fun py(y: Float): Float = y / 17f * height

        val path = Path().apply {
            moveTo(px(14.0622f), py(0f))
            lineTo(px(16.7836f), py(8.10854f))
            cubicTo(
                px(18.4352f), py(13.0296f),
                px(22.8319f), py(16.5149f),
                px(28f), py(17f)
            )
            lineTo(px(0f), py(17f))
            cubicTo(
                px(5.24247f), py(16.515f),
                px(9.705f), py(12.9824f),
                px(11.3802f), py(7.99115f)
            )
            lineTo(px(14.0622f), py(0f))
            close()
        }

        drawPath(path = path, color = iconColor)
    }
}
