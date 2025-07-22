package band.effective.office.smsrouter.domain.model

import kotlinx.serialization.Serializable

/**
 * Data class representing the settings for the SMS router.
 *
 * @property simCards List of SIM cards with their webhook URLs and secret keys
 */
@Serializable
data class Settings(
    val simCards: List<SimCardSettings> = emptyList()
)

/**
 * Data class representing the settings for a SIM card.
 *
 * @property simId Unique identifier for the SIM card
 * @property simName Name of the SIM card
 * @property webhookUrl URL to which SMS messages will be forwarded
 * @property secretKey Secret key for webhook authorization
 */
@Serializable
data class SimCardSettings(
    val simId: String,
    val simName: String,
    val webhookUrl: String = "",
    val secretKey: String = ""
)