package band.effective.office.tablet.feature.main.presentation.slot.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.ui.arrow_to_down
import band.effective.office.tablet.feature.main.presentation.slot.SlotUi
import org.jetbrains.compose.resources.painterResource

@Composable
fun MultiSlotView(
    modifier: Modifier = Modifier,
    slotUi: SlotUi.MultiSlot,
    onItemClick: (SlotUi) -> Unit,
    onCancel: (SlotUi.DeleteSlot) -> Unit
) {
    Column(Modifier.animateContentSize()) {
        CommonSlotView(modifier, slotUi) {
            val rotateDegrees = if (slotUi.isOpen) 180f else 0f
            Image(
                modifier = Modifier.fillMaxHeight().rotate(rotateDegrees),
                painter = painterResource(band.effective.office.tablet.core.ui.Res.drawable.arrow_to_down),
                contentDescription = null
            )
        }
        if (slotUi.isOpen) {
            slotUi.subSlots.forEach {
                Spacer(Modifier.height(20.dp))
                SlotView(
                    slotUi = it,
                    onClick = onItemClick,
                    onCancel = onCancel
                )
            }
        }
    }
}