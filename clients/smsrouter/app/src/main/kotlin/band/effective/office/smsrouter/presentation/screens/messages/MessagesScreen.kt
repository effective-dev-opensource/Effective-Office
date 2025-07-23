package band.effective.office.smsrouter.presentation.screens.messages

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import band.effective.office.smsrouter.presentation.model.SmsLog
import band.effective.office.smsrouter.presentation.model.SmsStatus
import band.effective.office.smsrouter.presentation.ui.theme.SmsRouterTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.koin.compose.koinInject

@Composable
fun MessagesScreen() {
    val viewModel: MessageScreenViewModel = koinInject()
    MessagesScreenContent(viewModel)
}

@Composable
private fun MessagesScreenContent(viewModel: MessageScreenViewModel = koinInject()) {
    val smsLogs by viewModel.smsLogs.collectAsState()

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SMS Messages (${smsLogs.size})",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { viewModel.clearAllLogs() }
                ) {
                    Text("Clear All")
                }
            }

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
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }
    val formattedDate = dateFormat.format(Date(log.timestamp))

    // Define status colors with animation
    val targetColor = when (log.status) {
        SmsStatus.DELIVERED -> Color.Green
        SmsStatus.ERROR -> Color.Red
        SmsStatus.IN_PROGRESS -> Color.Blue
    }

    // Animate color change
    val statusColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 500),
        label = "statusColorAnimation"
    )

    // Create a mutable state for the scale animation
    var targetScale by remember { mutableStateOf(1f) }

    // Animate the scale based on the targetScale state
    val scale by animateFloatAsState(
        targetValue = targetScale,
        animationSpec = tween(durationMillis = 500),
        label = "statusScaleAnimation"
    )

    // Trigger pulse animation when status changes
    LaunchedEffect(log.status) {
        // Start with a larger scale
        targetScale = 1.2f
        // Then animate back to normal
        kotlinx.coroutines.delay(50) // Small delay to ensure the larger scale is applied
        targetScale = 1f
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

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Status indicator with animations
                Box(
                    modifier = Modifier
                        // Apply scale to create pulse effect
                        .scale(scale)
                        .background(statusColor, shape = MaterialTheme.shapes.small)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .animateContentSize(
                            animationSpec = tween(durationMillis = 300)
                        )
                ) {
                    Text(
                        text = when {
                            // Show retry count for IN_PROGRESS status
                            log.status == SmsStatus.IN_PROGRESS && log.retryCount > 0 ->
                                "${log.status.name} (Retry ${log.retryCount})"
                            // Show retry count for ERROR status if retries were attempted
                            log.status == SmsStatus.ERROR && log.retryCount > 0 ->
                                "${log.status.name} (After ${log.retryCount} ${if (log.retryCount == 1) "retry" else "retries"})"
                            // Default case - just show the status name
                            else -> log.status.name
                        },
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }

                // SIM type indicator
                Box(
                    modifier = Modifier
                        .background(Color.Black, shape = MaterialTheme.shapes.small)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = log.simType,
                        color = Color.White,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Text(
            text = log.message,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        // Show error details if status is ERROR
        if (log.status == SmsStatus.ERROR && log.errorDetails != null) {
            Text(
                text = log.errorDetails,
                fontSize = 12.sp,
                color = Color.Red,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

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
