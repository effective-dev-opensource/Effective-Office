package band.effective.foosball.presentation.screens.tourGame

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import band.effective.foosball.presentation.components.CustomButton
import band.effective.foosball.ui.theme.BackgroundColor
import band.effective.foosball.ui.theme.DrukWide
import band.effective.foosball.ui.theme.Orange
import band.effective.foosball.ui.theme.Purple
import band.effective.foosball.ui.theme.Roboto
import band.effective.foosball.ui.theme.White
import com.example.effectivefoosball.R

@Composable
fun GracWinnerScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Box {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.grac_winner),
                    style = TextStyle(
                        color = White,
                        fontFamily = DrukWide,
                        fontSize = 58.sp
                    )
                )
                Text(
                    text = "Roman K.\nSvetlana M.",
                    style = TextStyle(
                        color = White,
                        fontFamily = DrukWide,
                        fontSize = 50.sp
                    )
                )
            }
        }
        Box {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.next_game),
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
                    Text(
                        text = "Lera M.\nPetya P.",
                        style = TextStyle(
                            color = White,
                            fontFamily = DrukWide,
                            fontSize = 40.sp
                        )
                    )
                    Text(
                        text = stringResource(id = R.string.vs),
                        style = TextStyle(
                            color = Orange,
                            fontFamily = DrukWide,
                            fontSize = 30.sp
                        )
                    )
                    Text(
                        text = "Lera M.\nPetya P.",
                        style = TextStyle(
                            color = White,
                            fontFamily = DrukWide,
                            fontSize = 40.sp
                        )
                    )
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .width(400.dp)
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
                        painter = painterResource(id = R.drawable.ic_invitation),
                        contentDescription = null,
                        tint = White
                    )
                    Text(
                        text = stringResource(id = R.string.invite_the_following_teams),
                        style = TextStyle(
                            color = Color.White,
                            fontFamily = Roboto,
                            fontSize = 20.sp
                        )
                    )
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
}