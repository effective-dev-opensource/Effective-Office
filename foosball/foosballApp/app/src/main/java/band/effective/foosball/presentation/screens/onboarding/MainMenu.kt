package band.effective.foosball.presentation.screens.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import band.effective.foosball.presentation.components.CustomButton
import band.effective.foosball.presentation.components.routes.Routes
import band.effective.foosball.ui.theme.LightBlack
import band.effective.foosball.ui.theme.Orange
import band.effective.foosball.ui.theme.Typography
import com.example.effectivefoosball.R

@Composable
fun MainMenu(navHostController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier = Modifier.padding(top = 50.dp),
                text = stringResource(R.string.select_play),
                style = Typography.displayMedium
            )
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentHeight()
                    .verticalScroll(
                        rememberScrollState()
                    ),
                horizontalArrangement = Arrangement.Center
            ) {
                CustomButton(
                    text = stringResource(R.string.tour_play),
                    onClick = { navHostController.navigate(Routes.ALERT_DIALOG) },
                    cornerRadius = 30.dp,
                    defaultColor = LightBlack,
                    border = BorderStroke(0.dp, LightBlack),
                    width = 318.dp,
                    height = 372.dp,
                )
                Spacer(modifier = Modifier.width(30.dp))
                CustomButton(
                    text = stringResource(R.string.play),
                    onClick = { navHostController.navigate(Routes.START_GAME_SCREEN) },
                    cornerRadius = 30.dp,
                    defaultColor = LightBlack,
                    border = BorderStroke(3.dp, Orange),
                    width = 318.dp,
                    height = 372.dp,
                )
                Spacer(modifier = Modifier.width(30.dp))
                CustomButton(
                    text = stringResource(R.string.competitive_game),
                    onClick = { navHostController.navigate(Routes.TEAM_FAST_GAME) },
                    cornerRadius = 30.dp,
                    defaultColor = LightBlack,
                    border = BorderStroke(0.dp, LightBlack),
                    width = 318.dp,
                    height = 372.dp,
                )
            }
        }
    }
}