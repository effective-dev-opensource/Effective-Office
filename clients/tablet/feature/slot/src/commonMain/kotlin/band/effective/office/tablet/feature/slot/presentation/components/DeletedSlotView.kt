package band.effective.office.tablet.feature.slot.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.cancel
import band.effective.office.tablet.core.ui.theme.h7
import band.effective.office.tablet.feature.slot.presentation.SlotUi
import org.jetbrains.compose.resources.stringResource

@Composable
fun DeletedSlotView(
    modifier: Modifier = Modifier,
    slotUi: SlotUi.DeleteSlot,
    onCancel: (SlotUi.DeleteSlot) -> Unit,
    paddingValues: PaddingValues
) {
    Box(modifier = modifier.height(IntrinsicSize.Min).width(IntrinsicSize.Min)) {
        var isCancelability by remember { mutableStateOf(true) }
        CommonSlotView(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxWidth(),
            slotUi = slotUi
        ) {
            if (isCancelability) {
                Text(
                    modifier = Modifier.clickable { onCancel(slotUi) },
                    text = stringResource(Res.string.cancel),
                    style = MaterialTheme.typography.h7,
                    color = Color.White
                )
            }
        }
        BorderIndicator(
            onDispose = {
                slotUi.onDelete()
                isCancelability = false
            }, 
            stokeWidth = 10.dp,
            initialProgress = slotUi.deletionProgress
        )
    }
}
