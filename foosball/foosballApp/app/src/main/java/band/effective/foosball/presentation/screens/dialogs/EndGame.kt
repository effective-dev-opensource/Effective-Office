package band.effective.foosball.presentation.screens.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import band.effective.foosball.presentation.components.routes.GameMode
import band.effective.foosball.presentation.screens.competitionGame.competitionDialog.CompetitiveGameEndButtons
import band.effective.foosball.presentation.screens.fastGame.fastDialog.FastGameEndButtons
import band.effective.foosball.presentation.screens.tourGame.tourDialog.TournamentGameEndButtons
import band.effective.foosball.ui.theme.DrukWide
import band.effective.foosball.ui.theme.LightBlack
import band.effective.foosball.ui.theme.White
import band.effective.foosball.vievmodel.ScoreViewModel
import com.example.effectivefoosball.R

@Composable
fun EndGame(
    navController: NavController,
    scoreViewModel: ScoreViewModel
) {
    val currentGameMode by scoreViewModel.currentGameMode.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(976.dp)
                .height(632.dp)
                .background(LightBlack, shape = RoundedCornerShape(30.dp))
                .clip(RoundedCornerShape(50.dp))
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(100.dp).fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.give_up),
                    style = TextStyle(
                        color = White,
                        fontFamily = DrukWide,
                        fontSize = 35.sp
                    )
                )

                when (currentGameMode) {
                    GameMode.FAST -> FastGameEndButtons(navController, scoreViewModel)
                    GameMode.COMPETITIVE -> CompetitiveGameEndButtons(navController, scoreViewModel)
                    GameMode.TOURNAMENT -> TournamentGameEndButtons(navController, scoreViewModel)
                    GameMode.MAIN_MANU -> TODO()
                }
            }
        }
    }
}
