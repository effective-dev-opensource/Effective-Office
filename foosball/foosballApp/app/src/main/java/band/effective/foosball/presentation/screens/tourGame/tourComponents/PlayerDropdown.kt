package band.effective.foosball.presentation.screens.tourGame.tourComponents

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import band.effective.foosball.ui.theme.LightBlack
import band.effective.foosball.ui.theme.Roboto
import com.example.effectivefoosball.R

@Composable
fun PlayerDropdown() {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .width(390.dp)
            .height(80.dp)
            .clip(shape = RoundedCornerShape(30.dp))
            .background(LightBlack, RoundedCornerShape(30.dp))
            .clickable { expanded = true }
            .padding(start = 30.dp, end = 30.dp)
    ) {
        Text(
            text = stringResource(id = R.string.player),
            modifier = Modifier.align(Alignment.CenterStart),
            style = TextStyle(
                color = Gray,
                fontSize = 30.sp,
                fontFamily = Roboto
            )
        )
        Icon(
            painter = painterResource(id = R.drawable.ic_unwarp),
            contentDescription = null,
            tint = Gray,
            modifier = Modifier.align(Alignment.CenterEnd)
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Тут должен быть выпадающий список
        }
    }
}
