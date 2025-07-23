package band.effective.office.smsrouter.data.mapper

import band.effective.office.smsrouter.data.models.MattermostSmsDataRequest
import band.effective.office.smsrouter.data.models.SmsDataRequest
import band.effective.office.smsrouter.data.models.TelegramSmsDataRequest
import band.effective.office.smsrouter.domain.model.SmsData
import band.effective.office.smsrouter.domain.model.WebhookType

internal class SmsDataDtoMapper {

    /**
     * Maps a domain SmsData object to a data layer SmsDataRequest object.
     *
     * @param domain The domain SmsData object to map.
     * @param webhookType The type of webhook service to use.
     * @param chatId The chat ID for Telegram webhooks (only used when webhookType is TELEGRAM).
     * @return The appropriate SmsDataRequest implementation based on the webhook type.
     */
    fun map(
        domain: SmsData,
        webhookType: WebhookType,
        chatId: String = ""
    ): SmsDataRequest = when (webhookType) {
        WebhookType.MATTERMOST -> MattermostSmsDataRequest(
            text = domain.messageBody,
        )
        WebhookType.TELEGRAM -> TelegramSmsDataRequest(
            chat_id = chatId,
            text = domain.messageBody,
        )
    }
}
