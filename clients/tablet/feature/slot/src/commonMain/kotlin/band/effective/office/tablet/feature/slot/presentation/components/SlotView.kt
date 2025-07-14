package band.effective.office.tablet.feature.slot.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import band.effective.office.tablet.core.domain.model.Slot
import band.effective.office.tablet.core.domain.util.freeTime
import band.effective.office.tablet.core.ui.Res
import band.effective.office.tablet.core.ui.empty_slot
import band.effective.office.tablet.core.ui.event_slot
import band.effective.office.tablet.core.ui.loading_slot
import band.effective.office.tablet.core.ui.multislot
import band.effective.office.tablet.core.ui.theme.LocalCustomColorsPalette
import band.effective.office.tablet.core.ui.theme.subslotColor
import band.effective.office.tablet.feature.slot.presentation.SlotUi
import org.jetbrains.compose.resources.stringResource

@Composable
fun SlotView(
    slotUi: SlotUi,
    onClick: SlotUi.() -> Unit,
    onCancel: (SlotUi.DeleteSlot) -> Unit
) {
    val borderShape = CircleShape
    val itemModifier = Modifier
        .fillMaxWidth()
        .clip(borderShape)
        .background(MaterialTheme.colorScheme.surface)
        .clickable { slotUi.onClick() }
        .run {
            if (slotUi !is SlotUi.DeleteSlot) border(
                width = 5.dp,
                color = slotUi.borderColor(),
                shape = borderShape
            ).padding(vertical = 15.dp, horizontal = 30.dp) else this
        }

    when (slotUi) {
        is SlotUi.DeleteSlot -> DeletedSlotView(
            modifier = itemModifier,
            slotUi = slotUi,
            onCancel = onCancel,
            paddingValues = PaddingValues(vertical = 15.dp, horizontal = 30.dp)
        )

        is SlotUi.MultiSlot -> MultiSlotView(
            modifier = itemModifier,
            slotUi = slotUi,
            onItemClick = { item -> item.onClick() },
            onCancel = onCancel
        )

        is SlotUi.SimpleSlot -> CommonSlotView(
            modifier = itemModifier,
            slotUi = slotUi
        )

        is SlotUi.NestedSlot -> CommonSlotView(
            modifier = itemModifier,
            slotUi = slotUi
        )

        is SlotUi.LoadingSlot -> LoadingSlotView(
            modifier = itemModifier,
            slotUi = slotUi
        )
    }
}

@Composable
private fun SlotUi.borderColor() = when (this) {
    is SlotUi.DeleteSlot -> Color.Gray
    is SlotUi.MultiSlot -> LocalCustomColorsPalette.current.busyStatus
    is SlotUi.NestedSlot -> subslotColor
    is SlotUi.SimpleSlot -> when (slot) {
        is Slot.EmptySlot -> LocalCustomColorsPalette.current.freeStatus
        is Slot.EventSlot -> LocalCustomColorsPalette.current.busyStatus
        is Slot.MultiEventSlot -> LocalCustomColorsPalette.current.busyStatus
        is Slot.LoadingEventSlot -> LocalCustomColorsPalette.current.loadingColor
    }

    is SlotUi.LoadingSlot -> LocalCustomColorsPalette.current.loadingColor
}

@Composable
fun Slot.subtitle() = when (this) {
    is Slot.EmptySlot -> stringResource(Res.string.empty_slot, freeTime().toString())
    is Slot.EventSlot -> stringResource(Res.string.event_slot, eventInfo.organizer.fullName)
    is Slot.MultiEventSlot -> stringResource(Res.string.multislot, events.size.toString())
    is Slot.LoadingEventSlot -> stringResource(Res.string.loading_slot, eventInfo.organizer.fullName)
}