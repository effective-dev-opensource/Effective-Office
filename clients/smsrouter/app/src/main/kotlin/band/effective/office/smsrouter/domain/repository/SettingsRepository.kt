package band.effective.office.smsrouter.domain.repository

import band.effective.office.smsrouter.domain.model.Settings
import band.effective.office.smsrouter.domain.model.SimCardSettings
import band.effective.office.smsrouter.domain.model.WebhookType
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing application settings.
 */
interface SettingsRepository {
    /**
     * Get the current settings as a Flow.
     * @return Flow of Settings
     */
    fun getSettings(): Flow<Settings>

    /**
     * Get the webhook URL for a specific SIM card.
     * @param simId The ID of the SIM card
     * @return The webhook URL or null if not found
     */
    suspend fun getWebhookUrl(simId: String): String?

    /**
     * Get the secret key for a specific SIM card.
     * @param simId The ID of the SIM card
     * @return The secret key or null if not found
     */
    suspend fun getSecretKey(simId: String): String?

    /**
     * Get the webhook type for a specific SIM card.
     * @param simId The ID of the SIM card
     * @return The webhook type or MATTERMOST if not found
     */
    suspend fun getWebhookType(simId: String): WebhookType

    /**
     * Get the chat ID for a specific SIM card (for Telegram webhooks).
     * @param simId The ID of the SIM card
     * @return The chat ID or empty string if not found
     */
    suspend fun getChatId(simId: String): String

    /**
     * Save the settings.
     * @param settings The settings to save
     */
    suspend fun saveSettings(settings: Settings)

    /**
     * Update the settings for a specific SIM card.
     * @param simCardSettings The SIM card settings to update
     */
    suspend fun updateSimCardSettings(simCardSettings: SimCardSettings)
}
