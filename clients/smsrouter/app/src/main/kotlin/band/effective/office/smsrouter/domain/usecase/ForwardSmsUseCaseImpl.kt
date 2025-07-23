package band.effective.office.smsrouter.domain.usecase

import android.util.Log
import band.effective.office.smsrouter.domain.Either
import band.effective.office.smsrouter.domain.ErrorResponse
import band.effective.office.smsrouter.domain.model.SmsData
import band.effective.office.smsrouter.domain.repository.SettingsRepository
import band.effective.office.smsrouter.domain.repository.SmsForwardingRepository
import band.effective.office.smsrouter.domain.repository.SmsLogsRepository
import band.effective.office.smsrouter.domain.unbox
import band.effective.office.smsrouter.presentation.model.SmsLog
import band.effective.office.smsrouter.presentation.model.SmsStatus

internal class ForwardSmsUseCaseImpl(
    private val smsForwardingRepository: SmsForwardingRepository,
    private val settingsRepository: SettingsRepository,
    private val smsLogsRepository: SmsLogsRepository,
) : ForwardSmsUseCase {

    companion object {
        private const val TAG = "ForwardSmsUseCase"
    }

    override suspend fun invoke(sms: SmsData): Either<ErrorResponse, Unit> {
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

        Log.d(TAG, "Sending SMS data to backend...")

        val simId = sms.simId
        val webhookUrl = settingsRepository.getWebhookUrl(simId).orEmpty()
        val secretKey = settingsRepository.getSecretKey(simId).orEmpty()

        val result = smsForwardingRepository.forwardSms(
            url = webhookUrl,
            secretKey = secretKey,
            smsData = sms,
        )

        result.unbox(
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
            }
        )

        return result
    }
}
