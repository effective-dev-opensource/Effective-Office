package band.effective.foosball.presentation.screens.competitionGame

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import band.effective.foosball.presentation.components.CustomButton
import band.effective.foosball.presentation.components.routes.Routes
import band.effective.foosball.ui.theme.DrukWide
import band.effective.foosball.ui.theme.Orange
import band.effective.foosball.ui.theme.White
import com.example.effectivefoosball.R

@Composable
fun TeamsShowScreen(
    redTeamMember1: String,
    redTeamMember2: String,
    blueTeamMember1: String,
    blueTeamMember2: String,
    navController: NavController
) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
            ) {
                Text(
                    text = "$redTeamMember1\n$redTeamMember2",
                    style = androidx.compose.ui.text.TextStyle(
                        color = White,
                        fontFamily = DrukWide,
                        fontSize = 35.sp
                    ),
                    modifier = Modifier.padding(end = 50.dp)
                )

                Text(
                    text = "VS",
                    style = androidx.compose.ui.text.TextStyle(
                        color = Orange,
                        fontFamily = DrukWide,
                        fontSize = 30.sp
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                Text(
                    text = "$blueTeamMember1\n$blueTeamMember2",
                    style = androidx.compose.ui.text.TextStyle(
                        color = White,
                        fontFamily = DrukWide,
                        fontSize = 35.sp
                    ),
                    modifier = Modifier.padding(start = 50.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            CustomButton(
                defaultColor = Orange,
                text = stringResource(id = R.string.start_game),
                onClick = {
                    navController.navigate(
                        "${Routes.SCORE_SCREEN_COMP}/$redTeamMember1/$redTeamMember2/$blueTeamMember1/$blueTeamMember2"
                    )
                },
                width = 340.dp,
                height = 98.dp,
                border = BorderStroke(0.dp, Orange),
                cornerRadius = 80.dp
            )
        }
    }
}
