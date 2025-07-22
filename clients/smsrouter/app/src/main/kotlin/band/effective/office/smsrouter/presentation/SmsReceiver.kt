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
            smsLogsRepository.put(
                SmsLog(
                    sms.sender,
                    sms.messageBody,
                    sms.operatorName,
                    System.currentTimeMillis()
                )
            )
            sendSmsToBackend(sms)
        }
    }

    private fun sendSmsToBackend(sms: SmsData) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d(TAG, "Sending SMS data to backend...")
            forwardSmsUseCase(sms).unbox(
                errorHandler = { error ->
                    Log.e(TAG, "Failed to send SMS data to backend: ${error.code} - ${error.description}")
                },
                successHandler = {
                    Log.d(TAG, "SMS data sent to backend successfully")
                },
            )
        }
    }
}

data class SmsLog(
    val sender: String,
    val message: String,
    val simType: String,
    val timestamp: Long
)