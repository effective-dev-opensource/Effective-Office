package band.effective.foosball.presentation.screens.tourGame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import band.effective.foosball.presentation.components.routes.Routes
import band.effective.foosball.presentation.screens.tourGame.tourComponents.TeamRow
import band.effective.foosball.ui.theme.Orange
import band.effective.foosball.ui.theme.Typography
import com.example.effectivefoosball.R

@Composable
fun TeamDistributionScreen(teamCount: Int, navController: NavController) {
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.distribution_of_teams),
            style = Typography.displayMedium,
            modifier = Modifier.padding(top = 32.dp, bottom = 30.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(45.dp)
        ) {
            items(teamCount) { index ->
                TeamRow(index + 1)
            }
        }

        Button(
            onClick = { navController.navigate(Routes.TEAM_DRAG_DROP_LIST_SCREEN) },
            modifier = Modifier
                .width(400.dp)
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
