package band.effective.office.backend.feature.notifications.service

/**
 * Interface for sending notifications on topics
 */
interface INotificationSender {

    /**
     * Sends message about topic modification
     */
    fun sendEmptyMessage(topic: String)
    fun sendDataMessage(topic: String, data: Map<String, String>)
}