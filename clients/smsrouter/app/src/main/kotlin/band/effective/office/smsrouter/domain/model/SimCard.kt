package band.effective.office.smsrouter.domain.model

/**
 * Data class representing a SIM card.
 *
 * @property subscriptionId Unique identifier for the subscription
 * @property simId Unique identifier for the SIM card
 * @property simName Name of the SIM card (carrier name)
 * @property simSlotIndex Index of the SIM slot
 * @property phoneNumber Phone number associated with the SIM card
 */
data class SimCard(
    val subscriptionId: Int,
    val simId: String,
    val simName: String,
    val simSlotIndex: Int,
    val phoneNumber: String = ""
)