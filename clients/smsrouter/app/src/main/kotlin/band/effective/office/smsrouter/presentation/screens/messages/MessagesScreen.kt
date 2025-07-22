package band.effective.office.smsrouter.presentation.screens.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import band.effective.office.smsrouter.domain.repository.SmsLogsRepository
import band.effective.office.smsrouter.presentation.SmsLog
import band.effective.office.smsrouter.presentation.ui.theme.SmsRouterTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.koin.compose.koinInject

@Composable
fun MessagesScreen() {
    MessagesScreenContent()
}

@Composable
private fun MessagesScreenContent() {
    // Use mutableStateOf to hold the SMS logs
    val smsLogsRepository: SmsLogsRepository = koinInject()
    val smsLogs by smsLogsRepository.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (smsLogs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No SMS messages received yet.\nWaiting for incoming messages...",
                    textAlign = TextAlign.Center,
                    fontSize = 16.sp
                )
            }
        } else {
            Text(
                text = "SMS Messages (${smsLogs.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(smsLogs) { log ->
                    SmsLogItem(log)
                    HorizontalDivider()
                }
            }
        }
    }
}

@Composable
fun SmsLogItem(log: SmsLog) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(log.timestamp))

    // Determine color based on operator name
    // Default to green for SIM1/first SIM and blue for SIM2/second SIM
    val simColor = when {
        log.simType.contains("SIM1", ignoreCase = true) -> Color(0xFF4CAF50) // Green
        log.simType.contains("SIM2", ignoreCase = true) -> Color(0xFF2196F3) // Blue
        else -> {
            // If it's not a default SIM name, use a hash of the operator name to generate a consistent color
            val hash = log.simType.hashCode()
            val hue = (hash % 360).toFloat() // Use hash to get a hue value between 0-359
            Color.hsv(hue, 0.8f, 0.9f) // Create a color with that hue
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = log.sender,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Box(
                modifier = Modifier
                    .background(simColor, shape = MaterialTheme.shapes.small)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = log.simType,
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }

        Text(
            text = log.message,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Text(
            text = formattedDate,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SmsLogScreenPreview() {
    SmsRouterTheme {
        MessagesScreenContent()
    }
}
