package band.effective.office.backend.feature.notifications.service

import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.Message
import org.slf4j.LoggerFactory

/**
 * Class for sending Firebase cloud messages
 */
class FcmNotificationSender(
    private val fcm: FirebaseMessaging,
) : INotificationSender {
    private val logger = LoggerFactory.getLogger(FcmNotificationSender::class.java)

    /**
     * Sends empty FCM message on topic
     */
    override fun sendEmptyMessage(topic: String) {
        logger.info("Sending an FCM message on $topic topic")
        val msg: Message = Message.builder()
            .setTopic(topic)
            .putData("message", "$topic was changed")
            .build()
        fcm.send(msg)
    }

    override fun sendDataMessage(topic: String, data: Map<String, String>) {
        logger.info("Sending data FCM message on $topic topic: $data")
        val msg = Message.builder()
            .setTopic(topic)
            .putAllData(data)
            .build()
        try {
            val messageId = fcm.send(msg)
            logger.info("Successfully sent data message to topic: $topic with data: $data, messageId: $messageId")
        } catch (e: Exception) {
            logger.error("Failed to send data message to topic $topic: ${e.message}")
        }
    }
}
