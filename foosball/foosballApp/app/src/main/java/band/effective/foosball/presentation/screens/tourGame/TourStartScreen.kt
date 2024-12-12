package band.effective.foosball.presentation.screens.tourGame

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import band.effective.foosball.presentation.components.routes.Constants
import band.effective.foosball.presentation.components.routes.Routes
import band.effective.foosball.ui.theme.BackgroundColor
import band.effective.foosball.ui.theme.DrukWide
import band.effective.foosball.ui.theme.LightBlack
import band.effective.foosball.ui.theme.Orange
import band.effective.foosball.ui.theme.Purple
import band.effective.foosball.ui.theme.Roboto
import band.effective.foosball.ui.theme.Score
import band.effective.foosball.ui.theme.Typography
import band.effective.foosball.ui.theme.White
import com.example.effectivefoosball.R

@Composable
fun TourStartScreen(navController: NavController) {
    var count by remember { mutableIntStateOf(Constants.MIN_LENGTH_COUNTER_TEAM) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.tour_game),
            style = Typography.displayMedium,
            modifier = Modifier.padding(top = 32.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.count_team_tour_game),
                style = TextStyle(
                    fontFamily = DrukWide,
                    fontSize = 35.sp,
                    color = White
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(60.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { if (count > Constants.MIN_LENGTH_COUNTER_TEAM) count-- },
                    modifier = Modifier
                        .size(60.dp)
                        .background(LightBlack, shape = RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = "-",
                        style = TextStyle(
                            color = White,
                            fontFamily = Roboto,
                            fontSize = 30.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .background(LightBlack, shape = RoundedCornerShape(22.dp))
                        .border(3.dp, Score, shape = RoundedCornerShape(22.dp))
                        .clip(RoundedCornerShape(22.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = count.toString(),
                        style = TextStyle(
                            color = White,
                            fontFamily = Roboto,
                            fontSize = 50.sp
                        )
                    )
                }

                IconButton(
                    onClick = { if (count < Constants.MAX_LENGTH_COUNTER_TEAM) count++ },
                    modifier = Modifier
                        .size(60.dp)
                        .background(LightBlack, shape = RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = "+",
                        style = TextStyle(
                            color = White,
                            fontFamily = Roboto,
                            fontSize = 30.sp
                        )
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { },
                modifier = Modifier
                    .width(500.dp)
                    .height(98.dp)
                    .border(3.dp, Purple, shape = RoundedCornerShape(80.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = BackgroundColor),
                shape = RoundedCornerShape(80.dp)
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_history),
                        contentDescription = null,
                        tint = White
                    )
                    Text(
                        text = stringResource(id = R.string.game_history),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    navController.navigate("${Routes.TEAM_DISTRIBUTION_SCREEN}/$count")
                },
                modifier = Modifier
                    .width(390.dp)
                    .height(98.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                shape = RoundedCornerShape(80.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.next),
                    style = Typography.labelLarge
                )
            }
        }
    }
}
