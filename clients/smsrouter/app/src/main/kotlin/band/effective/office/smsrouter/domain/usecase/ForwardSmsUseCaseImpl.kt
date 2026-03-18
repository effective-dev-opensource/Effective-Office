package band.effective.office.smsrouter.domain.usecase

import android.util.Log
import band.effective.office.smsrouter.data.SmsApiServiceImpl
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
        // Get the current retry count (should be 0 for new messages)
        val retryCount = SmsApiServiceImpl.getRetryCount(smsId)

        val initialLog = SmsLog(
            id = smsId,
            sender = sms.sender,
            message = sms.messageBody,
            simType = sms.operatorName,
            timestamp = System.currentTimeMillis(),
            status = SmsStatus.IN_PROGRESS,
            retryCount = retryCount
        )

        // Add the log to the repository
        smsLogsRepository.put(initialLog)

        Log.d(TAG, "Sending SMS data to backend...")

        val simId = sms.simId
        val webhookUrl = settingsRepository.getWebhookUrl(simId).orEmpty()
        val secretKey = settingsRepository.getSecretKey(simId).orEmpty()
        val webhookType = settingsRepository.getWebhookType(simId)
        val chatId = settingsRepository.getChatId(simId)

        // Create a callback to update the log with retry information in real-time
        val retryCallback: (String, Int) -> Unit = { id, retryCount ->
            // Get the current state of logs
            val currentLogs = smsLogsRepository.state.value
            // Find the log with the matching ID
            val currentLog = currentLogs.find { it.id == id }

            // If the log exists, update it with the new retry count
            // Otherwise, fall back to the initial log
            val updatedLog = currentLog?.copy(
                retryCount = retryCount
            ) ?: initialLog.copy(
                retryCount = retryCount
            )

            smsLogsRepository.put(updatedLog)
        }

        val result = smsForwardingRepository.forwardSms(
            url = webhookUrl,
            secretKey = secretKey,
            smsData = sms,
            webhookType = webhookType,
            chatId = chatId,
            smsId = smsId,
            onRetry = retryCallback
        )

        result.unbox(
            errorHandler = { error ->
                Log.e(TAG, "Failed to send SMS data to backend: ${error.code} - ${error.description}")

                // Get the current retry count
                val retryCount = SmsApiServiceImpl.getRetryCount(smsId)

                // Update log with ERROR status, error details, and retry count
                val updatedLog = initialLog.copy(
                    status = SmsStatus.ERROR,
                    errorDetails = "Error ${error.code}: ${error.description}",
                    retryCount = retryCount
                )
                smsLogsRepository.put(updatedLog)
            },
            successHandler = {
                Log.d(TAG, "SMS data sent to backend successfully")

                // On success, retry count should be 0, but we'll get it anyway for consistency
                val retryCount = SmsApiServiceImpl.getRetryCount(smsId)

                // Update log with DELIVERED status and retry count
                val updatedLog = initialLog.copy(
                    status = SmsStatus.DELIVERED,
                    retryCount = retryCount
                )
                smsLogsRepository.put(updatedLog)
            }
        )

        return result
    }
}
