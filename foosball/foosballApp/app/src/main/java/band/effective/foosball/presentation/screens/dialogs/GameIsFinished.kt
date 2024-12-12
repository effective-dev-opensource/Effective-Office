package band.effective.foosball.presentation.screens.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import band.effective.foosball.presentation.components.CustomButton
import band.effective.foosball.presentation.components.routes.Routes
import band.effective.foosball.ui.theme.DrukWide
import band.effective.foosball.ui.theme.LightBlack
import band.effective.foosball.ui.theme.Orange
import band.effective.foosball.ui.theme.Purple
import band.effective.foosball.ui.theme.White
import com.example.effectivefoosball.R

@Composable
fun GameIsFinished(
    navController: NavController,
    // content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(984.dp)
                .height(646.dp)
                .background(LightBlack, shape = RoundedCornerShape(30.dp))
                .clip(RoundedCornerShape(50.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.game_is_finished),
                    style = TextStyle(
                        color = White,
                        fontFamily = DrukWide,
                        fontSize = 50.sp
                    )
                )
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 100.dp, vertical = 20.dp)
                ) {
                    Button(
                        onClick = { /* TODO */ },
                        colors = ButtonDefaults.buttonColors(LightBlack),
                        shape = RoundedCornerShape(80.dp),
                        border = BorderStroke(3.dp, Purple),
                        modifier = Modifier
                            .weight(1f)
                            .height(122.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_pausa),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Text(
                                text = stringResource(id = R.string.stop_recording),
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        }
                    }
                    Button(
                        onClick = { /* TODO */ },
                        colors = ButtonDefaults.buttonColors(LightBlack),
                        shape = RoundedCornerShape(80.dp),
                        border = BorderStroke(3.dp, Purple),
                        modifier = Modifier
                            .weight(1f)
                            .height(122.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_stop_streem),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Text(
                                text = stringResource(id = R.string.stop_stream),
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        }
                    }
                    Button(
                        onClick = { /* TODO */ },
                        colors = ButtonDefaults.buttonColors(LightBlack),
                        shape = RoundedCornerShape(80.dp),
                        border = BorderStroke(3.dp, Purple),
                        modifier = Modifier
                            .weight(1f)
                            .height(122.dp)
                            .padding(horizontal = 8.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_save),
                                contentDescription = null,
                                tint = Color.White
                            )
                            Text(
                                text = stringResource(id = R.string.saave_recording),
                                fontSize = 20.sp,
                                color = Color.White
                            )
                        }
                    }
                }
                CustomButton(
                    defaultColor = Orange,
                    text = stringResource(id = R.string.new_game),
                    onClick = { navController.navigate(Routes.SCORE_SCREEN) },
                    width = 339.dp,
                    height = 98.dp,
                    border = BorderStroke(0.dp, Orange),
                    cornerRadius = 80.dp
                )
                // content()
            }
        }
    }
}
