package band.effective.office.smsrouter.data.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import band.effective.office.smsrouter.domain.model.Settings
import band.effective.office.smsrouter.domain.model.SimCardSettings
import band.effective.office.smsrouter.domain.model.WebhookType
import band.effective.office.smsrouter.domain.repository.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

/**
 * Implementation of SettingsRepository using SharedPreferences.
 */
internal class SettingsRepositoryImpl(
    context: Context
) : SettingsRepository {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFERENCES_NAME, Context.MODE_PRIVATE
    )

    private val json = Json { ignoreUnknownKeys = true }
    private val _settingsFlow = MutableStateFlow(loadSettings())

    override fun getSettings(): Flow<Settings> = _settingsFlow.asStateFlow()

    override suspend fun getWebhookUrl(simId: String): String? = withContext(Dispatchers.IO) {
        _settingsFlow.value.simCards.find { it.simId == simId }?.webhookUrl
    }

    override suspend fun getSecretKey(simId: String): String? = withContext(Dispatchers.IO) {
        _settingsFlow.value.simCards.find { it.simId == simId }?.secretKey
    }

    override suspend fun getWebhookType(simId: String): WebhookType = withContext(Dispatchers.IO) {
        _settingsFlow.value.simCards.find { it.simId == simId }?.webhookType ?: WebhookType.MATTERMOST
    }

    override suspend fun getChatId(simId: String): String = withContext(Dispatchers.IO) {
        _settingsFlow.value.simCards.find { it.simId == simId }?.chatId ?: ""
    }

    override suspend fun saveSettings(settings: Settings) = withContext(Dispatchers.IO) {
        val settingsJson = json.encodeToString(settings)
        sharedPreferences.edit { putString(KEY_SETTINGS, settingsJson) }
        _settingsFlow.value = settings
    }

    override suspend fun updateSimCardSettings(simCardSettings: SimCardSettings) =
        withContext(Dispatchers.IO) {
            val currentSettings = _settingsFlow.value
            val updatedSimCards = currentSettings.simCards.map {
                if (it.simId == simCardSettings.simId) simCardSettings else it
            }.toMutableList()

            // If the SIM card doesn't exist yet, add it
            if (!currentSettings.simCards.any { it.simId == simCardSettings.simId }) {
                updatedSimCards.add(simCardSettings)
            }

            val updatedSettings = currentSettings.copy(simCards = updatedSimCards)
            saveSettings(updatedSettings)
        }

    private fun loadSettings(): Settings {
        val settingsJson = sharedPreferences.getString(KEY_SETTINGS, null)
        return if (settingsJson != null) {
            try {
                json.decodeFromString(settingsJson)
            } catch (e: Exception) {
                Settings()
            }
        } else {
            Settings()
        }
    }

    companion object {
        private const val PREFERENCES_NAME = "sms_router_settings"
        private const val KEY_SETTINGS = "settings"
    }
}
