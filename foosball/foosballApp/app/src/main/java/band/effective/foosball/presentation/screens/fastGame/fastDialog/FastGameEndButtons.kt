package band.effective.foosball.presentation.screens.fastGame.fastDialog

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
fun FastGameEndButtons(navController: NavController, scoreViewModel: ScoreViewModel) {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
    Column {
        CustomButton(
            defaultColor = LightBlack,
            text = stringResource(R.string.yes),
            onClick = {
                scoreViewModel.resetScore()
                navController.navigate(Routes.NEW_FAST_GAME_DIALOG)
            },
            width = 584.dp,
            height = 132.dp,
            border = BorderStroke(3.dp, Orange),
            cornerRadius = 80.dp
        )
        CustomButton(
            defaultColor = Orange,
            text = stringResource(R.string.no_continue_game),
            onClick = { onBackPressedDispatcher?.onBackPressed() },
            width = 584.dp,
            height = 132.dp,
            border = BorderStroke(0.dp, Orange),
            cornerRadius = 80.dp
        )
    }
}
