package band.effective.office.smsrouter.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SubscriptionManager
import android.util.Log
import band.effective.office.smsrouter.domain.model.SimCard
import band.effective.office.smsrouter.domain.model.SmsData
import band.effective.office.smsrouter.domain.provider.SimCardProvider
import band.effective.office.smsrouter.domain.repository.SmsLogsRepository
import band.effective.office.smsrouter.domain.unbox
import band.effective.office.smsrouter.domain.usecase.ForwardSmsUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SmsReceiver : BroadcastReceiver(), KoinComponent {

    private val forwardSmsUseCase: ForwardSmsUseCase by inject()
    private val simCardProvider: SimCardProvider by inject()
    private val smsLogsRepository: SmsLogsRepository by inject()

    companion object {
        private const val TAG = "SmsReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val bundle = intent.extras ?: return
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val subscriptionId = bundle.getInt("subscription", SubscriptionManager.INVALID_SUBSCRIPTION_ID)

        val simCard: SimCard? = simCardProvider.getSimCardBySubscriptionId(subscriptionId)

        val smsList = messages.map {
            SmsData(
                sender = it.displayOriginatingAddress,
                operatorName = simCard?.simName.orEmpty(),
                messageBody = it.displayMessageBody,
                recipientPhoneNumber = simCard?.phoneNumber.orEmpty(),
                simId = simCard?.simId.orEmpty(),
            )
        }

        for (sms in smsList) {
            sendSmsToBackend(sms)
        }
    }

    private fun sendSmsToBackend(sms: SmsData) {
        // Create a unique ID for this SMS to track it
        val smsId = "${sms.sender}-${System.currentTimeMillis()}"

        // Create initial log with IN_PROGRESS status
        val initialLog = SmsLog(
            id = smsId,
            sender = sms.sender,
            message = sms.messageBody,
            simType = sms.operatorName,
            timestamp = System.currentTimeMillis(),
            status = SmsStatus.IN_PROGRESS
        )

        // Add the log to the repository
        smsLogsRepository.put(initialLog)

        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "Sending SMS data to backend...")
            forwardSmsUseCase(sms).unbox(
                errorHandler = { error ->
                    Log.e(TAG, "Failed to send SMS data to backend: ${error.code} - ${error.description}")

                    // Update log with ERROR status and error details
                    val updatedLog = initialLog.copy(
                        status = SmsStatus.ERROR,
                        errorDetails = "Error ${error.code}: ${error.description}"
                    )
                    smsLogsRepository.put(updatedLog)
                },
                successHandler = {
                    Log.d(TAG, "SMS data sent to backend successfully")

                    // Update log with DELIVERED status
                    val updatedLog = initialLog.copy(
                        status = SmsStatus.DELIVERED
                    )
                    smsLogsRepository.put(updatedLog)
                },
            )
        }
    }
}

data class SmsLog(
    val id: String,
    val sender: String,
    val message: String,
    val simType: String,
    val timestamp: Long,
    val status: SmsStatus = SmsStatus.IN_PROGRESS,
    val errorDetails: String? = null
)

enum class SmsStatus {
    DELIVERED,
    ERROR,
    IN_PROGRESS
}
