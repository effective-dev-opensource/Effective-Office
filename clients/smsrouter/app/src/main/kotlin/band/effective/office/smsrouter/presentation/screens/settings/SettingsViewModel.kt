package band.effective.office.smsrouter.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import band.effective.office.smsrouter.domain.model.Settings
import band.effective.office.smsrouter.domain.model.SimCardSettings
import band.effective.office.smsrouter.domain.model.WebhookType
import band.effective.office.smsrouter.domain.provider.SimCardProvider
import band.effective.office.smsrouter.domain.repository.SettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val simcardProvider: SimCardProvider,
) : ViewModel() {

    // State
    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    // Effects
    private val _effect = MutableSharedFlow<Effect>()
    val effect: SharedFlow<Effect> = _effect.asSharedFlow()

    init {
        loadSettings()
        loadSimCards()
    }

    // Handle intents from the UI
    fun sendIntent(intent: Intent) {
        when (intent) {
            is Intent.UpdateWebhookUrl -> updateWebhookUrl(intent.simId, intent.url)
            is Intent.UpdateSecretKey -> updateSecretKey(intent.simId, intent.key)
            is Intent.UpdateWebhookType -> updateWebhookType(intent.simId, intent.webhookType)
            is Intent.UpdateChatId -> updateChatId(intent.simId, intent.chatId)
            is Intent.SaveSettings -> saveSettings()
            is Intent.ReloadSimCards -> loadSimCards()
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            settingsRepository.getSettings().collectLatest { settings ->
                _state.update { it.copy(settings = settings) }
            }
        }
    }

    private fun loadSimCards() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            delay(1000)
            val simCards = simcardProvider.getAvailableSimCards().map { simCard ->
                val existingSettings = _state.value.settings.simCards.find { it.simId == simCard.simId }
                SimCardUiModel(
                    simId = simCard.simId,
                    simName = simCard.simName,
                    webhookUrl = existingSettings?.webhookUrl ?: "",
                    secretKey = existingSettings?.secretKey ?: "",
                    webhookType = existingSettings?.webhookType ?: WebhookType.MATTERMOST,
                    chatId = existingSettings?.chatId ?: ""
                )
            }

            _state.update { it.copy(simCards = simCards, isLoading = false) }
        }
    }

    private fun updateWebhookUrl(simId: String, url: String) {
        _state.update { currentState ->
            val updatedSimCards = currentState.simCards.map { simCard ->
                if (simCard.simId == simId) {
                    simCard.copy(webhookUrl = url)
                } else {
                    simCard
                }
            }
            currentState.copy(simCards = updatedSimCards)
        }
    }

    private fun updateSecretKey(simId: String, key: String) {
        _state.update { currentState ->
            val updatedSimCards = currentState.simCards.map { simCard ->
                if (simCard.simId == simId) {
                    simCard.copy(secretKey = key)
                } else {
                    simCard
                }
            }
            currentState.copy(simCards = updatedSimCards)
        }
    }

    private fun updateWebhookType(simId: String, webhookType: WebhookType) {
        _state.update { currentState ->
            val updatedSimCards = currentState.simCards.map { simCard ->
                if (simCard.simId == simId) {
                    simCard.copy(webhookType = webhookType)
                } else {
                    simCard
                }
            }
            currentState.copy(simCards = updatedSimCards)
        }
    }

    private fun updateChatId(simId: String, chatId: String) {
        _state.update { currentState ->
            val updatedSimCards = currentState.simCards.map { simCard ->
                if (simCard.simId == simId) {
                    simCard.copy(chatId = chatId)
                } else {
                    simCard
                }
            }
            currentState.copy(simCards = updatedSimCards)
        }
    }

    private fun saveSettings() {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isSaving = true) }

                // Convert UI models to domain models
                val simCardSettings = _state.value.simCards.map { uiModel ->
                    SimCardSettings(
                        simId = uiModel.simId,
                        simName = uiModel.simName,
                        webhookUrl = uiModel.webhookUrl,
                        secretKey = uiModel.secretKey,
                        webhookType = uiModel.webhookType,
                        chatId = uiModel.chatId
                    )
                }

                // Save each SIM card's settings
                simCardSettings.forEach { settings ->
                    settingsRepository.updateSimCardSettings(settings)
                }

                _effect.emit(Effect.SettingsSaved)
                _state.update { it.copy(isSaving = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isSaving = false, error = e.message ?: "Failed to save settings") }
                _effect.emit(Effect.ShowError("Failed to save settings: ${e.message}"))
            }
        }
    }

    // State class
    data class State(
        val settings: Settings = Settings(),
        val simCards: List<SimCardUiModel> = emptyList(),
        val isLoading: Boolean = true,
        val isSaving: Boolean = false,
        val error: String? = null
    )

    // UI model for SIM card settings
    data class SimCardUiModel(
        val simId: String,
        val simName: String,
        val webhookUrl: String,
        val secretKey: String,
        val webhookType: WebhookType = WebhookType.MATTERMOST,
        val chatId: String = ""
    )

    // Intent sealed class for user actions
    sealed class Intent {
        data class UpdateWebhookUrl(val simId: String, val url: String) : Intent()
        data class UpdateSecretKey(val simId: String, val key: String) : Intent()
        data class UpdateWebhookType(val simId: String, val webhookType: WebhookType) : Intent()
        data class UpdateChatId(val simId: String, val chatId: String) : Intent()
        object SaveSettings : Intent()
        object ReloadSimCards : Intent()
    }

    // Effect sealed class for one-time events
    sealed class Effect {
        object SettingsSaved : Effect()
        object RequestPermission : Effect()
        data class ShowError(val message: String) : Effect()
    }
}
