package band.effective.office.elevator.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import band.effective.office.elevator.EffectiveTheme

@Composable
fun ScrollPicker(
    modifier: Modifier = Modifier,
    items: List<String>,
) {
    val itemHeight = 32.dp

    Box(
        modifier = modifier
            .background(
                color = EffectiveTheme.colors.background.primary,
                shape = RoundedCornerShape(16.dp),
            )
            .border(
                width = 1.dp,
                color = EffectiveTheme.colors.stroke.primary,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(16.dp),
    ) {
        Box(
            modifier = modifier
                .width(IntrinsicSize.Max)
                .height(itemHeight)
                .align(Alignment.Center)
                .background(
                    color = EffectiveTheme.colors.background.secondary,
                    shape = RoundedCornerShape(16.dp),
                )
        )
        PickerColumn(
            modifier = Modifier.align(Alignment.Center),
            items = items,
            itemHeight = itemHeight
        )
    }
}

@Composable
fun ScrollPicker(
    modifier: Modifier = Modifier,
    startItems: List<String>,
    endItems: List<String>,
) {
    val itemHeight = 32.dp

    Box(
        modifier = modifier
            .background(
                color = EffectiveTheme.colors.background.primary,
                shape = RoundedCornerShape(16.dp),
            )
            .border(
                width = 1.dp,
                color = EffectiveTheme.colors.stroke.primary,
                shape = RoundedCornerShape(16.dp),
            )
            .padding(16.dp),
    ) {
        Box(
            modifier = modifier
                .width(IntrinsicSize.Max)
                .height(itemHeight)
                .align(Alignment.Center)
                .background(
                    color = EffectiveTheme.colors.background.secondary,
                    shape = RoundedCornerShape(16.dp),
                )
        )
        Row(
            modifier = Modifier.align(Alignment.Center),
            horizontalArrangement = Arrangement.spacedBy(40.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PickerColumn(items = startItems, itemHeight = itemHeight)
            PickerColumn(items = endItems, itemHeight = itemHeight)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PickerColumn(
    modifier: Modifier = Modifier,
    items: List<String>,
    itemHeight: Dp,
    showLimit: Int = 3,
) {
    val state = rememberLazyListState(Int.MAX_VALUE / 2)
    val centerIndex by derivedStateOf { state.firstVisibleItemIndex + showLimit / 2 }

    LazyColumn(
        modifier = modifier.height(showLimit * itemHeight),
        state = state,
        flingBehavior = rememberSnapFlingBehavior(state)
    ) {
        items(Int.MAX_VALUE) { index ->
            val textColor = if (index == centerIndex) {
                EffectiveTheme.colors.text.accent
            } else {
                EffectiveTheme.colors.text.secondary
            }
            val item = items[index % items.size]
            Box(modifier = Modifier.height(itemHeight)) {
                Text(
                    text = item,
                    textAlign = TextAlign.Center,
                    style = EffectiveTheme.typography.mMedium.copy(color = textColor),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
