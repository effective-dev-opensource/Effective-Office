package band.effective.foosball.presentation.screens.tourGame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import band.effective.foosball.presentation.screens.tourGame.tourComponents.DragDropList
import band.effective.foosball.ui.theme.DrukWide
import band.effective.foosball.ui.theme.Orange
import band.effective.foosball.ui.theme.Roboto
import band.effective.foosball.ui.theme.TeamList
import band.effective.foosball.ui.theme.Typography
import band.effective.foosball.ui.theme.White
import band.effective.foosball.vievmodel.GameOrderViewModel
import com.example.effectivefoosball.R

data class Game(
    val id: Int,
    val team1: List<String>,
    val team2: List<String>
)

@Composable
fun GameOrderScreen(viewModel: GameOrderViewModel, navController: NavController) {
    val games = viewModel.games

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.sequence_of_games),
            style = Typography.displayMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Column(modifier = Modifier.width(80.dp)) {
                repeat(5) { index ->
                    Box(
                        modifier = Modifier
                            .height(98.dp)
                            .fillMaxSize()
                            .padding(end = 5.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${stringResource(id = R.string.game)} ${index + 1}",
                            style = TextStyle(
                                color = White,
                                fontFamily = Roboto,
                                fontSize = 20.sp
                            )
                        )
                    }
                }
            }
            DragDropList(
                items = games,
                onMove = { from, to ->
                    viewModel.reorderGames(from, to)
                },
                modifier = Modifier.width(712.dp)
            ) { game ->
                GameItem(game)
            }
        }

        Button(
            onClick = {
                val firstGame = games.firstOrNull()
                if (firstGame != null) {
                    val team1String = firstGame.team1.joinToString(",")
                    val team2String = firstGame.team2.joinToString(",")
                    navController.navigate("game1/$team1String/$team2String")
                }
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


@Composable
fun GameItem(game: Game) {
    Card(
        modifier = Modifier
            .height(98.dp)
            .fillMaxSize()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = TeamList),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_dragburger),
                contentDescription = null,
                tint = White,
                modifier = Modifier.padding(end = 16.dp)
            )
            TeamNames(game.team1)
            Text(
                text = stringResource(id = R.string.vs),
                style = TextStyle(
                    color = Orange,
                    fontFamily = DrukWide,
                    fontSize = 25.sp
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            TeamNames(game.team2)
        }
    }
}

@Composable
fun TeamNames(names: List<String>, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        names.forEach { name ->
            Text(name, style = TextStyle(fontFamily = Roboto, fontSize = 25.sp, color = White))
        }
    }
}

fun generateGames(): List<Game> {
    return listOf(
        Game(1, listOf("Grisha T.", "Misha J."), listOf("Petya L.", "Maya K.")),
        Game(2, listOf("Genia D.", "Toria N."), listOf("Grisha F.", "Misha R.")),
        Game(3, listOf("Katia H.", "Olga R."), listOf("Petya F.", "Yana R.")),
        Game(4, listOf("Ivan K.", "Jack A."), listOf("Kate U.", "Olga R.")),
        Game(5, listOf("Roman K.", "Grisha R."), listOf("Arina F.", "Yana N."))
    )
}