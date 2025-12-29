package band.effective.office.smsrouter.data.provider

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SubscriptionManager
import android.util.Log
import androidx.core.content.ContextCompat
import band.effective.office.smsrouter.domain.model.SimCard
import band.effective.office.smsrouter.domain.provider.SimCardProvider

/**
 * Implementation of [SimCardProvider] that uses [SubscriptionManager] to get SIM card information.
 */
class SimCardProviderImpl(
    private val context: Context,
    private val subscriptionManager: SubscriptionManager
) : SimCardProvider {

    private val tag = "SimCardProviderImpl"

    override fun getAvailableSimCards(): List<SimCard> {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(tag, "READ_PHONE_STATE permission not granted")
            return emptyList()
        }

        return try {
            val activeSubscriptions = subscriptionManager.activeSubscriptionInfoList ?: emptyList()
            
            activeSubscriptions.map { subscriptionInfo ->
                val simId = subscriptionInfo.subscriptionId.toString()
                val simName = subscriptionInfo.displayName.toString()
                val simSlotIndex = subscriptionInfo.simSlotIndex
                val phoneNumber = getPhoneNumber(subscriptionInfo.subscriptionId)
                
                SimCard(
                    subscriptionId = subscriptionInfo.subscriptionId,
                    simId = simId,
                    simName = simName,
                    simSlotIndex = simSlotIndex,
                    phoneNumber = phoneNumber
                )
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting available SIM cards: ${e.message}")
            emptyList()
        }
    }

    override fun getSimCardBySubscriptionId(subscriptionId: Int): SimCard? {
        return getAvailableSimCards().find { it.subscriptionId == subscriptionId }
    }

    override fun getSimCardBySlotIndex(simSlotIndex: Int): SimCard? {
        return getAvailableSimCards().find { it.simSlotIndex == simSlotIndex }
    }

    private fun getPhoneNumber(subscriptionId: Int): String {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return ""
        }

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                subscriptionManager.getPhoneNumber(subscriptionId)
            } else {
                subscriptionManager.activeSubscriptionInfoList
                    ?.firstOrNull { it.subscriptionId == subscriptionId }?.number.orEmpty()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting phone number: ${e.message}")
            ""
        }
    }
}