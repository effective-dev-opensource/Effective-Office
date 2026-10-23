package band.effective.office.tablet.feature.slot.presentation.components

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
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.arrow_to_down
import band.effective.office.tablet.core.ui.res.painterResource
import band.effective.office.tablet.feature.slot.presentation.SlotUi

@Composable
fun MultiSlotView(
    modifier: Modifier = Modifier,
    slotUi: SlotUi.MultiSlot,
    onItemClick: SlotUi.() -> Unit,
) {
    Column(Modifier.animateContentSize()) {
        CommonSlotView(
            modifier = modifier,
            slotUi = slotUi
        ) {
            Image(
                modifier = Modifier
                    .fillMaxHeight()
                    .rotate(if (slotUi.isOpen) 180f else 0f),
                painter = painterResource(Res.drawable.arrow_to_down),
                contentDescription = null
            )
        }

        if (slotUi.isOpen) {
            slotUi.subSlots.forEach {
                Spacer(Modifier.height(20.dp))
                SlotView(
                    slotUi = it,
                    onClick = onItemClick,
                    onToggle = { /*Nothing*/ },
                )
            }
        }
    }
}