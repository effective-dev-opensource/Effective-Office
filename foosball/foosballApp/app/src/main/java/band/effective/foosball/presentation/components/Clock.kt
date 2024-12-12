package band.effective.foosball.presentation.components

import android.os.CountDownTimer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import band.effective.foosball.presentation.components.routes.Constants
import band.effective.foosball.ui.theme.Typography
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Clock() {
    var currentTime by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timer = object : CountDownTimer(Long.MAX_VALUE, Constants.COUNT_DOWN_INTERVAL.toLong()) {
            override fun onTick(millisUntilFinished: Long) {
                currentTime = getCurrentDate()
            }

            override fun onFinish() {
                this.start()
            }
        }
        timer.start()
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Text(
            text = currentTime,
            style = Typography.displayMedium
        )
    }
}

private fun getCurrentDate(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date())
}
