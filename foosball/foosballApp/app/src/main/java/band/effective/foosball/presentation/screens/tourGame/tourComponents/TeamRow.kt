package band.effective.foosball.presentation.screens.tourGame.tourComponents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import band.effective.foosball.ui.theme.Roboto
import band.effective.foosball.ui.theme.White
import com.example.effectivefoosball.R

@Composable
fun TeamRow(teamNumber: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${stringResource(id = R.string.team)} $teamNumber",
            style = TextStyle(
                color = White,
                fontSize = 40.sp,
                fontFamily = Roboto
            ),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PlayerDropdown()
            Text(
                text = "/",
                style = TextStyle(
                    color = White,
                    fontFamily = Roboto,
                    fontSize = 40.sp
                )
            )
            PlayerDropdown()
        }
    }
}
