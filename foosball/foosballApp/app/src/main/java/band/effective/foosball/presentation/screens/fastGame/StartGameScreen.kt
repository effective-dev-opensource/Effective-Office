package band.effective.foosball.presentation.screens.fastGame

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import band.effective.foosball.presentation.components.routes.Routes
import band.effective.foosball.ui.theme.Orange
import com.example.effectivefoosball.R

@Composable
fun StartGameScreen(navHostController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    navHostController.navigate(Routes.SCORE_SCREEN)
                },
                colors = ButtonDefaults.buttonColors(Orange),
                shape = RoundedCornerShape(80.dp),
                modifier = Modifier
                    .width(686.dp)
                    .height(118.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = stringResource(id = R.string.start_game),
                        fontSize = 24.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.ic_next),
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }
        }
    }
}
