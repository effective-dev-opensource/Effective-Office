package band.effective.foosball.presentation.screens.competitionGame

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import band.effective.foosball.presentation.components.routes.Routes
import band.effective.foosball.ui.theme.Blue
import band.effective.foosball.ui.theme.DrukWide
import band.effective.foosball.ui.theme.Red
import band.effective.foosball.ui.theme.Roboto
import band.effective.foosball.ui.theme.Typography
import band.effective.foosball.ui.theme.White
import band.effective.foosball.vievmodel.ScoreViewModel
import com.example.effectivefoosball.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun WinnerScreenComp(
    navController: NavController,
    scoreViewModel: ScoreViewModel,
    redTeamMember1: String,
    redTeamMember2: String,
    blueTeamMember1: String,
    blueTeamMember2: String,
    redTeamScore: Int,
    blueTeamScore: Int
) {
    val winningTeam = if (redTeamScore > blueTeamScore) {
        "$redTeamMember1\n$redTeamMember2"
    } else {
        "$blueTeamMember1\n$blueTeamMember2"
    }

    LaunchedEffect(Unit) {
        delay(3000) // Задержка в 3 секунды
        navController.navigate(Routes.NEW_COMP_GAME_DIALOG) {
            popUpTo(Routes.WINNER_SCREEN_COMP) { inclusive = true }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.winner_member),
            style = Typography.displayMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = stringResource(id = R.string.winner_team),
            style = Typography.displayMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 90.dp)
        )
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .border(
                    width = 2.dp,
                    color = if (redTeamScore > blueTeamScore) Red else Blue,
                    shape = RoundedCornerShape(20.dp)
                )
                .width(746.dp)
                .height(238.dp)
                .padding(30.dp)
        ) {
            Text(
                text = winningTeam,
                style = TextStyle(
                    color = White,
                    fontFamily = DrukWide,
                    fontSize = 50.sp
                )
            )
        }
    }
}
