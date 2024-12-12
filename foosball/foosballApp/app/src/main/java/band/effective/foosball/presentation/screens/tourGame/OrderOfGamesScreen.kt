package band.effective.foosball.presentation.screens.tourGame

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import band.effective.foosball.presentation.components.CustomButton
import band.effective.foosball.ui.theme.DrukWide
import band.effective.foosball.ui.theme.Orange
import band.effective.foosball.ui.theme.White
import com.example.effectivefoosball.R

@Composable
fun OrderOfGamesScreen(
    team1: List<String>,
    team2: List<String>
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Text(
            text = stringResource(id = R.string.game),
            style = TextStyle(
                color = White,
                fontFamily = DrukWide,
                fontSize = 60.sp
            )
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Отображение первой команды
            Column {
                team1.forEach { player ->
                    Text(
                        text = player,
                        style = TextStyle(
                            color = White,
                            fontFamily = DrukWide,
                            fontSize = 50.sp
                        )
                    )
                }
            }
            Text(
                text = "vs",
                style = TextStyle(
                    color = Orange,
                    fontFamily = DrukWide,
                    fontSize = 40.sp
                )
            )
            // Отображение второй команды
            Column {
                team2.forEach { player ->
                    Text(
                        text = player,
                        style = TextStyle(
                            color = White,
                            fontFamily = DrukWide,
                            fontSize = 50.sp
                        )
                    )
                }
            }
        }
        CustomButton(
            defaultColor = Orange,
            text = stringResource(id = R.string.start_game),
            onClick = { /*TODO*/ },
            width = 400.dp,
            height = 98.dp,
            cornerRadius = 80.dp,
            border = BorderStroke(3.dp, Orange)
        )
    }
}
