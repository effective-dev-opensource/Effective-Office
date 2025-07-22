package band.effective.office.smsrouter.presentation

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Telephony
import android.telephony.SubscriptionManager
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresPermission
import androidx.core.content.ContextCompat
import band.effective.office.smsrouter.domain.model.SmsData
import band.effective.office.smsrouter.domain.unbox
import band.effective.office.smsrouter.domain.usecase.ForwardSmsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SmsReceiver : BroadcastReceiver(), KoinComponent {

    private val forwardSmsUseCase: ForwardSmsUseCase by inject()

    companion object {
        private const val TAG = "SmsReceiver"

        // StateFlow to store and observe SMS logs
        private val _smsLogs = MutableStateFlow<List<SmsLog>>(emptyList())
        val smsLogs: StateFlow<List<SmsLog>> = _smsLogs.asStateFlow()

        // Function to add a new SMS log
        fun addSmsLog(log: SmsLog) {
            _smsLogs.update { currentLogs ->
                currentLogs + log
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val bundle = intent.extras ?: return
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            val subscriptionId = bundle.getInt("subscription", SubscriptionManager.INVALID_SUBSCRIPTION_ID)

            for (message in messages) {
                val sender = message.displayOriginatingAddress
                val messageBody = message.displayMessageBody
                val simSlotIndex = getSimSlotIndex(context, subscriptionId)
                val operatorName = getOperatorName(context, simSlotIndex)
                val recipientPhoneNumber = getPhoneNumberForSms(subscriptionId, context)

                // Log the SMS details
                Log.d(TAG, "SMS from $sender on $operatorName: $messageBody")

                // Show Toast notification
                Toast.makeText(
                    context,
                    "SMS from $sender on $operatorName: $messageBody",
                    Toast.LENGTH_LONG
                ).show()

                Log.e("SMS_RECEIVER", "SMS from $sender on $recipientPhoneNumber ( $operatorName ): $messageBody")


                // Add to our logs
                addSmsLog(SmsLog(sender, messageBody, operatorName, System.currentTimeMillis()))

                // Send SMS data to the backend
                sendSmsToBackend(sender, operatorName, messageBody, recipientPhoneNumber)
            }
        }
    }

    private fun getSimSlotIndex(context: Context, subscriptionId1: Int): Int {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            throw IllegalStateException("This feature requires Android 5.1 (API 22) or higher")
        }

        // Проверка разрешения
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            throw SecurityException("READ_PHONE_STATE permission not granted")
        }

        val subscriptionId = subscriptionId1

        val subscriptionManager =
            context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager
                ?: throw IllegalStateException("Could not access SubscriptionManager")

        val activeSubscriptions = subscriptionManager.activeSubscriptionInfoList
            ?: throw IllegalStateException("No active subscriptions found")

        // Находим соответствующий subscriptionId
        val info = activeSubscriptions.firstOrNull { it.subscriptionId == subscriptionId }
            ?: throw IllegalStateException("Subscription ID $subscriptionId not found in active subscriptions")

        return info.simSlotIndex
    }

    private fun getOperatorName(context: Context, simSlotIndex: Int): String {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP_MR1) {
            throw IllegalStateException("This feature requires Android Lollipop MR1 (API 22) or higher")
        }

        // Check if we have the necessary permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            throw SecurityException("READ_PHONE_STATE permission not granted")
        }

        val subscriptionManager =
            context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager
                ?: throw IllegalStateException("Could not access SubscriptionManager")

        // Get the list of active subscriptions
        val subscriptionInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            subscriptionManager.activeSubscriptionInfoList
        } else {
            subscriptionManager.activeSubscriptionInfoList
        } ?: throw IllegalStateException("No active subscriptions found")

        // Check if simSlotIndex is valid
        if (simSlotIndex >= subscriptionInfoList.size) {
            throw IndexOutOfBoundsException("Invalid SIM slot index: $simSlotIndex, max index is ${subscriptionInfoList.size - 1}")
        }

        // Find the subscription info for the given SIM slot
        val subscriptionInfo = subscriptionInfoList.find { it.simSlotIndex == simSlotIndex }
            ?: throw IllegalStateException("No subscription info found for SIM slot index: $simSlotIndex")

        subscriptionInfoList.forEach { info ->
            Log.e("SMS_RECEIVER", "SIM slot ${info.simSlotIndex}: ${info.carrierName}")
        }

        // Get the carrier name (operator name)
        val carrierName = subscriptionInfo.carrierName?.toString()
            ?: throw IllegalStateException("No carrier name found for SIM slot index: $simSlotIndex")

        if (carrierName.isBlank()) {
            throw IllegalStateException("Carrier name is blank for SIM slot index: $simSlotIndex")
        }

        return carrierName
    }

    @RequiresPermission(anyOf = [Manifest.permission.READ_PHONE_NUMBERS, "carrier privileges", "android.permission.READ_PRIVILEGED_PHONE_STATE"])
    private fun getPhoneNumberForSms(subscriptionId: Int, context: Context): String {
        val subscriptionManager =
            context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager
                ?: throw IllegalStateException("Could not access SubscriptionManager")
        val info: String = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            subscriptionManager.getPhoneNumber(subscriptionId)
        } else {
            subscriptionManager.activeSubscriptionInfoList
                ?.firstOrNull { it.subscriptionId == subscriptionId }?.number.orEmpty()
        }

        return info
    }

    // Function to send SMS data to the backend
    private fun sendSmsToBackend(
        sender: String,
        operatorName: String,
        messageBody: String,
        recipientPhoneNumber: String = ""
    ) {
        val smsData = SmsData(sender, operatorName, messageBody, recipientPhoneNumber)

        Log.d(TAG, "Preparing to send SMS data to backend: $smsData")

        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "Sending SMS data to backend...")
            forwardSmsUseCase(smsData).unbox(
                errorHandler = { error ->
                    // TODO implement 3 times retry
                    Log.e(TAG, "Failed to send SMS data to backend: ${error.code} - ${error.description}")
                },
                successHandler = {
                    Log.d(TAG, "SMS data sent to backend successfully")
                },
            )
        }
    }
}

// Data class to represent an SMS log entry
data class SmsLog(
    val sender: String,
    val message: String,
    val simType: String,
    val timestamp: Long
)
