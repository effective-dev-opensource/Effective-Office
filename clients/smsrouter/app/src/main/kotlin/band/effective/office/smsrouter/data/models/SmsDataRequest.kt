package band.effective.office.smsrouter.data.models

import kotlinx.serialization.Serializable

/**
 * Base interface for SMS data requests.
 */
interface SmsDataRequest

/**
 * Mattermost-specific SMS data request.
 */
@Serializable
data class MattermostSmsDataRequest(
    val text: String,
) : SmsDataRequest

/**
 * Telegram-specific SMS data request.
 */
@Serializable
data class TelegramSmsDataRequest(
    val chat_id: String,
    val text: String,
) : SmsDataRequest
