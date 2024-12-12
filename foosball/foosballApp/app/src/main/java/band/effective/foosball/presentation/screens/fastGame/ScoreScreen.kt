package band.effective.foosball.presentation.screens.fastGame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import band.effective.foosball.presentation.components.Clock
import band.effective.foosball.presentation.components.CounterBox
import band.effective.foosball.ui.theme.Blue
import band.effective.foosball.ui.theme.Red
import band.effective.foosball.vievmodel.ScoreViewModel

@Composable
fun ScoreScreen(scoreViewModel: ScoreViewModel) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 80.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Clock()
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            CounterBox(Red, scoreViewModel.getLeftScore())
            Spacer(modifier = Modifier.width(16.dp))
            CounterBox(Blue, scoreViewModel.getRightScore())
        }
    }
}
