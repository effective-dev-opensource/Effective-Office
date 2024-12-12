package band.effective.foosball.presentation.screens.tourGame.tourDialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import band.effective.foosball.presentation.components.CustomButton
import band.effective.foosball.presentation.components.routes.Routes
import band.effective.foosball.ui.theme.LightBlack
import band.effective.foosball.ui.theme.Orange
import band.effective.foosball.vievmodel.ScoreViewModel
import com.example.effectivefoosball.R

@Composable
fun TournamentGameEndButtons(navController: NavController, scoreViewModel: ScoreViewModel) {
    Column {
        CustomButton(
            defaultColor = LightBlack,
            text = stringResource(R.string.no),
            onClick = {  },
            width = 584.dp,
            height = 132.dp,
            border = BorderStroke(3.dp, Orange),
            cornerRadius = 80.dp
        )
        CustomButton(
            defaultColor = Orange,
            text = stringResource(R.string.yes_save_game),
            onClick = {
                navController.navigate(Routes.GAME_IS_FINISHED)
            },
            width = 584.dp,
            height = 132.dp,
            border = BorderStroke(0.dp, Orange),
            cornerRadius = 80.dp
        )
    }
}