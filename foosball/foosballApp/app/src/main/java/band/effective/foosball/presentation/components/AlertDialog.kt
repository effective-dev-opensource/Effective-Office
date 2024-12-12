package band.effective.foosball.presentation.components

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import band.effective.foosball.presentation.components.routes.Routes
import band.effective.foosball.ui.theme.DrukWide
import band.effective.foosball.ui.theme.LightBlack
import band.effective.foosball.ui.theme.Orange
import band.effective.foosball.ui.theme.White
import com.example.effectivefoosball.R

@Composable
fun AlertDialog() {
    val onBackPressedDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher
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
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.coding),
                    style = TextStyle(
                        color = White,
                        fontFamily = DrukWide,
                        fontSize = 35.sp
                    )
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_codding),
                    contentDescription = null,
                    tint = White
                )
                CustomButton(
                    defaultColor = LightBlack,
                    text = stringResource(id = R.string.ok),
                    onClick = { onBackPressedDispatcher?.onBackPressed() },
                    width = 584.dp,
                    height = 132.dp,
                    border = BorderStroke(3.dp, Orange),
                    cornerRadius = 80.dp
                )
            }
        }
    }
}
