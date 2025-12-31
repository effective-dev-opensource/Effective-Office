package band.effective.office.smsrouter.domain.provider

import band.effective.office.smsrouter.domain.model.SimCard

/**
 * Interface for providing SIM card information.
 */
interface SimCardProvider {
    /**
     * Get a list of all available SIM cards.
     *
     * @return List of available SIM cards
     */
    fun getAvailableSimCards(): List<SimCard>

    /**
     * Get a SIM card by its subscription ID.
     *
     * @param subscriptionId The subscription ID of the SIM card
     * @return The SIM card with the given subscription ID, or null if not found
     */
    fun getSimCardBySubscriptionId(subscriptionId: Int): SimCard?

    /**
     * Get a SIM card by its SIM slot index.
     *
     * @param simSlotIndex The SIM slot index
     * @return The SIM card in the given SIM slot, or null if not found
     */
    fun getSimCardBySlotIndex(simSlotIndex: Int): SimCard?
}