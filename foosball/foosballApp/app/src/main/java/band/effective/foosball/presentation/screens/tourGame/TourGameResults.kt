package band.effective.foosball.presentation.screens.tourGame

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.graphics.shapes.CornerRounding
import androidx.graphics.shapes.RoundedPolygon
import band.effective.foosball.presentation.components.CustomButton
import band.effective.foosball.presentation.screens.tourGame.tourComponents.RoundedPolygonShape
import band.effective.foosball.ui.theme.DarkOrange
import band.effective.foosball.ui.theme.DrukWide
import band.effective.foosball.ui.theme.Gold
import band.effective.foosball.ui.theme.Orange
import band.effective.foosball.ui.theme.Roboto
import band.effective.foosball.ui.theme.Silver
import band.effective.foosball.ui.theme.White
import com.example.effectivefoosball.R

@Composable
fun TourGameResults() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = stringResource(id = R.string.tour_results),
            style = TextStyle(
                color = White,
                fontFamily = DrukWide,
                fontSize = 45.sp
            )
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(15.dp)
                    .width(478.dp)
                    .height(120.dp)
                    .background(Gold, shape = RoundedCornerShape(22.dp))
                    .border(2.dp, Orange, shape = RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    val hexagon = remember {
                        RoundedPolygon(
                            6,
                            rounding = CornerRounding(0.2f)
                        )
                    }
                    val clip = remember(hexagon) {
                        RoundedPolygonShape(polygon = hexagon)
                    }
                    Box(
                        modifier = Modifier
                            .clip(clip)
                            .background(White)
                            .size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.first_place),
                            style = TextStyle(
                                color = Orange,
                                fontFamily = Roboto,
                                fontSize = 40.sp
                            )
                        )
                    }
                    Text(
                        text = "Женя/Витя",
                        style = TextStyle(
                            color = White,
                            fontFamily = Roboto,
                            fontSize = 40.sp
                        )
                    )
                }
            }
            Box(
                modifier = Modifier
                    .padding(15.dp)
                    .width(238.dp)
                    .height(120.dp)
                    .background(Gold, shape = RoundedCornerShape(22.dp))
                    .border(2.dp, Orange, shape = RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "30 баллов!",
                    style = TextStyle(
                        color = White,
                        fontFamily = Roboto,
                        fontSize = 40.sp
                    )
                )
            }
        }
        Row {
            Box(
                modifier = Modifier
                    .padding(15.dp)
                    .width(478.dp)
                    .height(120.dp)
                    .background(Silver, shape = RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    val hexagon = remember {
                        RoundedPolygon(
                            6,
                            rounding = CornerRounding(0.2f)
                        )
                    }
                    val clip = remember(hexagon) {
                        RoundedPolygonShape(polygon = hexagon)
                    }
                    Box(
                        modifier = Modifier
                            .clip(clip)
                            .background(White)
                            .size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.second_place),
                            style = TextStyle(
                                color = Orange,
                                fontFamily = Roboto,
                                fontSize = 40.sp
                            )
                        )
                    }
                    Text(
                        text = "Гриша/Миша",
                        style = TextStyle(
                            color = White,
                            fontFamily = Roboto,
                            fontSize = 40.sp
                        )
                    )
                }
            }
            Box(
                modifier = Modifier
                    .padding(15.dp)
                    .width(238.dp)
                    .height(120.dp)
                    .background(Silver, shape = RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "25 баллов!",
                    style = TextStyle(
                        color = White,
                        fontFamily = Roboto,
                        fontSize = 40.sp
                    )
                )
            }
        }
        Row {
            Box(
                modifier = Modifier
                    .padding(15.dp)
                    .width(478.dp)
                    .height(120.dp)
                    .background(DarkOrange, shape = RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    val hexagon = remember {
                        RoundedPolygon(
                            6,
                            rounding = CornerRounding(0.2f)
                        )
                    }
                    val clip = remember(hexagon) {
                        RoundedPolygonShape(polygon = hexagon)
                    }
                    Box(
                        modifier = Modifier
                            .clip(clip)
                            .background(White)
                            .size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.third_place),
                            style = TextStyle(
                                color = Orange,
                                fontFamily = Roboto,
                                fontSize = 40.sp
                            )
                        )
                    }
                    Text(
                        text = "Катя/Оля",
                        style = TextStyle(
                            color = White,
                            fontFamily = Roboto,
                            fontSize = 40.sp
                        )
                    )
                }
            }
            Box(
                modifier = Modifier
                    .padding(15.dp)
                    .width(238.dp)
                    .height(120.dp)
                    .background(DarkOrange, shape = RoundedCornerShape(22.dp))
                    .clip(RoundedCornerShape(22.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "23 балла!",
                    style = TextStyle(
                        color = White,
                        fontFamily = Roboto,
                        fontSize = 40.sp
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
