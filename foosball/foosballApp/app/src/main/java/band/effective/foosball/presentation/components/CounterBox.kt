package band.effective.foosball.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import band.effective.foosball.presentation.components.routes.Constants
import band.effective.foosball.ui.theme.BackgroundColor
import band.effective.foosball.ui.theme.Typography

@Composable
fun CounterBox(color: Color, state: MutableState<Int>) {
    Box(
        modifier = Modifier
            .width(430.dp)
            .height(497.dp)
            .border(4.dp, color = color, shape = RoundedCornerShape(size = 30.dp))
            .background(color = BackgroundColor, shape = RoundedCornerShape(size = 30.dp)),
        contentAlignment = Alignment.Center,

    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(shape = RoundedCornerShape(size = 30.dp))
                    .fillMaxWidth()
                    .clickable {
                        if (state.value > Constants.MIN_LENGTH_COUNTER_BOX) {
                            state.value--
                        }
                    }
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(shape = RoundedCornerShape(size = 30.dp))
                    .fillMaxWidth()
                    .clickable {
                        if (state.value < Constants.MAX_LENGTH_COUNTER_BOX) {
                            state.value++
                        }
                    }
            )
        }
        Text(
            text = state.value.toString(),
            style = Typography.displayLarge
        )
    }
}
