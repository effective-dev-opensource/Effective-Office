package band.effective.office.smsrouter.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import band.effective.office.smsrouter.presentation.ui.theme.SmsRouterTheme
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val requiredPermissions = arrayOf(
        Manifest.permission.READ_SMS,
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.READ_PHONE_NUMBERS,
    )

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.entries.all { it.value }
        if (allGranted) {
            Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Some permissions were denied. App may not work properly.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Request permissions
        requestPermissions()

        setContent {
            SmsRouterTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = { Text("SMS Router") },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                ) { innerPadding ->
                    SmsLogScreen(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun requestPermissions() {
        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest)
        }
    }
}

@Composable
fun SmsLogScreen(modifier: Modifier = Modifier) {
    // Use mutableStateOf to hold the SMS logs
    var smsLogs by remember { mutableStateOf<List<SmsLog>>(emptyList()) }

    // Use LaunchedEffect to collect the flow
    LaunchedEffect(key1 = true) {
        SmsReceiver.Companion.smsLogs.collect { logs ->
            smsLogs = logs
        }
    }

    Column(
        modifier = modifier
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
                    Divider()
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
        SmsLogScreen()
    }
}
