package band.effective.office.smsrouter.presentation.screens.settings

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import band.effective.office.smsrouter.domain.model.WebhookType
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
        onWebhookTypeChanged = { simCard, webhookType ->
            viewModel.sendIntent(
                SettingsViewModel.Intent.UpdateWebhookType(
                    simId = simCard.simId,
                    webhookType = webhookType
                )
            )
        },
        onChatIdChanged = { simCard, chatId ->
            viewModel.sendIntent(
                SettingsViewModel.Intent.UpdateChatId(
                    simId = simCard.simId,
                    chatId = chatId
                )
            )
        },
        onSaveClick = {
            viewModel.sendIntent(SettingsViewModel.Intent.SaveSettings)
        },
        onCheckClick = { simCard ->
            viewModel.sendIntent(SettingsViewModel.Intent.CheckWebHook(simCard.simId))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreenContent(
    state: SettingsViewModel.State,
    snackbarHostState: SnackbarHostState,
    onWebhookUrlChanged: (SettingsViewModel.SimCardUiModel, String) -> Unit,
    onSecretKeyChanged: (SettingsViewModel.SimCardUiModel, String) -> Unit,
    onWebhookTypeChanged: (SettingsViewModel.SimCardUiModel, WebhookType) -> Unit,
    onChatIdChanged: (SettingsViewModel.SimCardUiModel, String) -> Unit,
    onSaveClick: () -> Unit,
    onCheckClick: (SettingsViewModel.SimCardUiModel) -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "SIM Card Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                },
            )
        },
        bottomBar = {
            BottomAppBar(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier
                        .fillMaxWidth(.8f)
                        .height(48.dp),
                    enabled = !state.isSaving,
                ) {
                    Text(text = if (state.isSaving) "Saving..." else "Save Settings")
                }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
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
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        items(state.simCards) { simCard ->
                            SimCardSettingsItem(
                                simCard = simCard,
                                onWebhookUrlChanged = { url -> onWebhookUrlChanged(simCard, url) },
                                onSecretKeyChanged = { key -> onSecretKeyChanged(simCard, key) },
                                onWebhookTypeChanged = { type ->
                                    onWebhookTypeChanged(simCard, type)
                                },
                                onChatIdChanged = { chatId -> onChatIdChanged(simCard, chatId) },
                                onCheckClick = { onCheckClick(simCard) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SimCardSettingsItemHeader(
    modifier: Modifier,
    simName: String,
    simId: String,
    onCheckClick: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            // SIM Card Name
            Text(
                text = simName,
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(8.dp))

            // SIM Card ID
            Text(
                text = "SIM ID: $simId",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Button(onClick = onCheckClick) {
            Text("Check")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SimCardSettingsItem(
    simCard: SettingsViewModel.SimCardUiModel,
    onWebhookUrlChanged: (String) -> Unit,
    onSecretKeyChanged: (String) -> Unit,
    onWebhookTypeChanged: (WebhookType) -> Unit,
    onChatIdChanged: (String) -> Unit,
    onCheckClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            SimCardSettingsItemHeader(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                simName = simCard.simName,
                simId = simCard.simId,
                onCheckClick = onCheckClick
            )

            // Webhook Type Dropdown
            Text(
                text = "Webhook Service",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = simCard.webhookType.name,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    singleLine = true
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    WebhookType.values().forEach { webhookType ->
                        DropdownMenuItem(
                            text = { Text(webhookType.name) },
                            onClick = {
                                onWebhookTypeChanged(webhookType)
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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

            // Chat ID (only for Telegram)
            if (simCard.webhookType == WebhookType.TELEGRAM) {
                OutlinedTextField(
                    value = simCard.chatId,
                    onValueChange = onChatIdChanged,
                    label = { Text("Telegram Chat ID") },
                    placeholder = { Text("Enter chat ID") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

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

@Preview(showBackground = true)
@Composable
private fun SettingsScreenContentPreview() {
    val snackbarHostState = remember { SnackbarHostState() }
    SettingsScreenContent(
        state = SettingsViewModel.State(
            isLoading = false,
            simCards = listOf(
                SettingsViewModel.SimCardUiModel(
                    simId = "1",
                    simName = "SIM 1",
                    webhookUrl = "https://example.com/webhook",
                    secretKey = "secret",
                    webhookType = WebhookType.MATTERMOST,
                    chatId = ""
                ),
                SettingsViewModel.SimCardUiModel(
                    simId = "2",
                    simName = "SIM 2",
                    webhookUrl = "https://t.me/bot",
                    secretKey = "tg-secret",
                    webhookType = WebhookType.TELEGRAM,
                    chatId = "-100123456"
                )
            )
        ),
        snackbarHostState = snackbarHostState,
        onWebhookUrlChanged = { _, _ -> },
        onSecretKeyChanged = { _, _ -> },
        onWebhookTypeChanged = { _, _ -> },
        onChatIdChanged = { _, _ -> },
        onSaveClick = {},
        onCheckClick = {}
    )
}

@Composable
@Preview
private fun PreviewSimCardSettingsItem() {
    Surface {
        SimCardSettingsItem(
            simCard = SettingsViewModel.SimCardUiModel(
                simId = "1",
                simName = "SIM 1",
                webhookUrl = "https://example.com/webhook",
                secretKey = "secret",
                webhookType = WebhookType.MATTERMOST,
                chatId = ""
            ),
            onWebhookUrlChanged = {},
            onSecretKeyChanged = {},
            onWebhookTypeChanged = {},
            onChatIdChanged = {},
            onCheckClick = {}
        )
    }
}

@Composable
@Preview
private fun PreviewSimCardSettingsItemHeader() {
    Surface() {
        SimCardSettingsItemHeader(Modifier.fillMaxWidth(), "Name", "123321", {})
    }
}