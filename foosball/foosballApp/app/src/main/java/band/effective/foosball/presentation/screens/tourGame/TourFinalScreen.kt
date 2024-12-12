package band.effective.foosball.presentation.screens.tourGame

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import band.effective.foosball.ui.theme.Roboto
import band.effective.foosball.ui.theme.TeamList
import band.effective.foosball.ui.theme.White
import com.example.effectivefoosball.R

@Composable
fun TourFinalScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = stringResource(id = R.string.tour_final),
            style = TextStyle(
                color = White,
                fontFamily = DrukWide,
                fontSize = 60.sp
            )
        )
        Text(
            text = stringResource(id = R.string.game_first_place),
            style = TextStyle(
                color = White,
                fontFamily = DrukWide,
                fontSize = 40.sp
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .padding(30.dp)
                    .width(434.dp)
                    .height(102.dp)
                    .background(color = TeamList, shape = RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Konstantin Y.\nAlexander L.",
                        style = TextStyle(
                            color = White,
                            fontFamily = Roboto,
                            fontSize = 32.sp
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = "15 б",
                        style = TextStyle(
                            color = Orange,
                            fontFamily = Roboto,
                            fontSize = 36.sp
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            Text(
                text = stringResource(id = R.string.vs),
                style = TextStyle(
                    color = Orange,
                    fontFamily = DrukWide,
                    fontSize = 30.sp
                )
            )
            Box(
                modifier = Modifier
                    .padding(30.dp)
                    .width(434.dp)
                    .height(102.dp)
                    .background(color = TeamList, shape = RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Anastasia D.\nMiroslava L.",
                        style = TextStyle(
                            color = White,
                            fontFamily = Roboto,
                            fontSize = 32.sp
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = "15 б",
                        style = TextStyle(
                            color = Orange,
                            fontFamily = Roboto,
                            fontSize = 36.sp
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        Text(
            text = stringResource(id = R.string.game_third_place),
            style = TextStyle(
                color = White,
                fontFamily = DrukWide,
                fontSize = 35.sp
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Box(
                modifier = Modifier
                    .padding(30.dp)
                    .width(434.dp)
                    .height(102.dp)
                    .background(color = TeamList, shape = RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Grisha\nMisha",
                        style = TextStyle(
                            color = White,
                            fontFamily = Roboto,
                            fontSize = 32.sp
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = "15 б",
                        style = TextStyle(
                            color = Orange,
                            fontFamily = Roboto,
                            fontSize = 36.sp
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            Text(
                text = stringResource(id = R.string.vs),
                style = TextStyle(
                    color = Orange,
                    fontFamily = DrukWide,
                    fontSize = 30.sp
                )
            )
            Box(
                modifier = Modifier
                    .padding(30.dp)
                    .width(434.dp)
                    .height(102.dp)
                    .background(color = TeamList, shape = RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Petya\nYana",
                        style = TextStyle(
                            color = White,
                            fontFamily = Roboto,
                            fontSize = 32.sp
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                    Text(
                        text = "15 б",
                        style = TextStyle(
                            color = Orange,
                            fontFamily = Roboto,
                            fontSize = 36.sp
                        ),
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
        CustomButton(
            defaultColor = Orange,
            text = stringResource(id = R.string.next),
            onClick = { /*TODO*/ },
            width = 400.dp,
            height = 98.dp,
            border = BorderStroke(0.dp, Orange),
            cornerRadius = 80.dp
        )
    }
}
