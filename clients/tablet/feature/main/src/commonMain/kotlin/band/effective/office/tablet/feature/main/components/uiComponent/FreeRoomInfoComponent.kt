package band.effective.office.tablet.feature.main.components.uiComponent

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.h5
import band.effective.office.tablet.core.ui.theme.roomInfoColor
import band.effective.office.tablet.feature.main.Res
import band.effective.office.tablet.feature.main.data_not_upd
import band.effective.office.tablet.feature.main.free_now
import org.jetbrains.compose.resources.stringResource

@Composable
fun FreeRoomInfoComponent(
    modifier: Modifier = Modifier,
    name: String,
    capacity: Int,
    isHaveTv: Boolean,
    electricSocketCount: Int,
    isError: Boolean,
) {
    CommonRoomInfoComponent(
        modifier = modifier,
        name = name,
        capacity = capacity,
        isHaveTv = isHaveTv,
        electricSocketCount = electricSocketCount,
        backgroundColor = LocalCustomColorsPalette.current.freeStatus,
        isError = isError
    ) {
        Text(
            text = stringResource(if (isError) Res.string.data_not_upd else Res.string.free_now),
            style = MaterialTheme.typography.h5,
            color = roomInfoColor,
        )
    }
}