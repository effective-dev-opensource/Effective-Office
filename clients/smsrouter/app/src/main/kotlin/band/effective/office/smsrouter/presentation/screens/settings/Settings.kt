package band.effective.office.smsrouter.presentation.screens.settings

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen() {
    val viewModel: SettingsViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Permission request launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Reload SIM cards if permission granted
            viewModel.sendIntent(SettingsViewModel.Intent.ReloadSimCards)
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Permission denied. Cannot access SIM information.")
            }
        }
    }

    // Check if permission is already granted
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
        }
    }

    // Collect effects
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SettingsViewModel.Effect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is SettingsViewModel.Effect.SettingsSaved -> {
                    snackbarHostState.showSnackbar("Settings saved successfully")
                }

                is SettingsViewModel.Effect.RequestPermission -> {
                    permissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                }
            }
        }
    }


    SettingsScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onWebhookUrlChanged = { simCard, url ->
            viewModel.sendIntent(
                SettingsViewModel.Intent.UpdateWebhookUrl(
                    simId = simCard.simId,
                    url = url
                )
            )
        },
        onSecretKeyChanged = { simCard, key ->
            viewModel.sendIntent(
                SettingsViewModel.Intent.UpdateSecretKey(
                    simId = simCard.simId,
                    key = key
                )
            )
        },
        onSaveClick = {
            viewModel.sendIntent(SettingsViewModel.Intent.SaveSettings)
        }
    )
}

@Composable
private fun SettingsScreenContent(
    state: SettingsViewModel.State,
    snackbarHostState: SnackbarHostState,
    onWebhookUrlChanged: (SettingsViewModel.SimCardUiModel, String) -> Unit,
    onSecretKeyChanged: (SettingsViewModel.SimCardUiModel, String) -> Unit,
    onSaveClick: () -> Unit,
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    // Show loading indicator
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.simCards.isEmpty() -> {
                    // Show empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.error ?: "No SIM cards found",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                else -> {
                    // Show SIM card settings
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "SIM Card Settings",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.simCards) { simCard ->
                                SimCardSettingsItem(
                                    simCard = simCard,
                                    onWebhookUrlChanged = { url -> onWebhookUrlChanged(simCard, url) },
                                    onSecretKeyChanged = { key -> onSecretKeyChanged(simCard, key) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onSaveClick,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = !state.isSaving
                        ) {
                            Text(text = if (state.isSaving) "Saving..." else "Save Settings")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SimCardSettingsItem(
    simCard: SettingsViewModel.SimCardUiModel,
    onWebhookUrlChanged: (String) -> Unit,
    onSecretKeyChanged: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // SIM Card Name
            Text(
                text = simCard.simName,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // SIM Card ID
            Text(
                text = "SIM ID: ${simCard.simId}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Webhook URL
            OutlinedTextField(
                value = simCard.webhookUrl,
                onValueChange = onWebhookUrlChanged,
                label = { Text("Webhook URL") },
                placeholder = { Text("https://example.com") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Secret Key
            OutlinedTextField(
                value = simCard.secretKey,
                onValueChange = onSecretKeyChanged,
                label = { Text("Secret Key") },
                placeholder = { Text("Enter secret key") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
        }
    }
}
