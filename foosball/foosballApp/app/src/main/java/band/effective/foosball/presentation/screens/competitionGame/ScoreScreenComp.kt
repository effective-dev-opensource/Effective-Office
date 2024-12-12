package band.effective.foosball.presentation.screens.competitionGame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import band.effective.foosball.presentation.components.Clock
import band.effective.foosball.presentation.components.CounterBox
import band.effective.foosball.presentation.components.routes.Constants
import band.effective.foosball.presentation.components.routes.Routes
import band.effective.foosball.ui.theme.Blue
import band.effective.foosball.ui.theme.DrukWide
import band.effective.foosball.ui.theme.Red
import band.effective.foosball.ui.theme.White
import band.effective.foosball.vievmodel.ScoreViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ScoreScreenComp(
    navController: NavController,
    scoreViewModel: ScoreViewModel,
    redTeamMember1: String,
    redTeamMember2: String,
    blueTeamMember1: String,
    blueTeamMember2: String
) {
    val leftScore = scoreViewModel.getLeftScore().value
    val rightScore = scoreViewModel.getRightScore().value

    LaunchedEffect(leftScore, rightScore) {
        if (leftScore == Constants.MAX_SCORE_COMP || rightScore == Constants.MAX_SCORE_COMP) {
            val gameDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            // Сохранение данных игры
            scoreViewModel.saveGameScore(
                gameDate,
                redTeamMember1 = redTeamMember1,
                redTeamMember2 = redTeamMember2,
                blueTeamMember1 = blueTeamMember1,
                blueTeamMember2 = blueTeamMember2
            )

            // Отправка данных в Supabase
            GlobalScope.launch(Dispatchers.IO) {
                scoreViewModel.sendGameScoreToSupabase(gameDate = gameDate)
                scoreViewModel.resetScore()
            }

            // Переход на экран победителя
            navController.navigate(Routes.WINNER_SCREEN_COMP) {
                popUpTo(Routes.SCORE_SCREEN) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 80.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Clock()
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.width(100.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier
                        .padding(bottom = 20.dp),
                    text = "$redTeamMember1\n$redTeamMember2",
                    style = TextStyle(
                        color = White,
                        fontFamily = DrukWide,
                        fontSize = 30.sp
                    )
                )
                CounterBox(Red, scoreViewModel.getLeftScore())
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 20.dp),
                    text = "$blueTeamMember1\n$blueTeamMember2",
                    style = TextStyle(
                        color = White,
                        fontFamily = DrukWide,
                        fontSize = 30.sp
                    )
                )
                CounterBox(Blue, scoreViewModel.getRightScore())
            }
            Spacer(modifier = Modifier.width(100.dp))
        }
    }
}
