package band.effective.foosball.presentation.screens.competitionGame.competitionDialog

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import band.effective.foosball.presentation.components.CustomButton
import band.effective.foosball.presentation.components.routes.Routes
import band.effective.foosball.ui.theme.LightBlack
import band.effective.foosball.ui.theme.Orange
import band.effective.foosball.vievmodel.ScoreViewModel
import com.example.effectivefoosball.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CompetitiveGameEndButtons(navController: NavController, scoreViewModel: ScoreViewModel) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    Column {
        CustomButton(
            defaultColor = LightBlack,
            text = stringResource(R.string.no),
            onClick = { onBackPressedDispatcher?.onBackPressed() },
            width = 584.dp,
            height = 132.dp,
            border = BorderStroke(3.dp, Orange),
            cornerRadius = 80.dp
        )
        CustomButton(
            defaultColor = Orange,
            text = stringResource(R.string.yes_save_game),
            onClick = {
                val gameDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
                    Date()
                )
                scoreViewModel.getLeftScore().value
                scoreViewModel.getRightScore().value
                val redTeamMember1 = scoreViewModel.redTeamMember1
                val redTeamMember2 = scoreViewModel.redTeamMember2
                val blueTeamMember1 = scoreViewModel.blueTeamMember1
                val blueTeamMember2 = scoreViewModel.blueTeamMember2

                System.currentTimeMillis() - scoreViewModel.startTime

                scoreViewModel.saveGameScore(
                    gameDate,
                    redTeamMember1,
                    redTeamMember2,
                    blueTeamMember1,
                    blueTeamMember2
                )
                GlobalScope.launch(Dispatchers.IO) {
                    scoreViewModel.sendGameScoreToSupabase(
                        gameDate = gameDate
                    )
                    scoreViewModel.resetScore()
                }
                navController.navigate(Routes.NEW_COMP_GAME_DIALOG)
            },
            width = 584.dp,
            height = 132.dp,
            border = BorderStroke(0.dp, Orange),
            cornerRadius = 80.dp
        )
    }
}
