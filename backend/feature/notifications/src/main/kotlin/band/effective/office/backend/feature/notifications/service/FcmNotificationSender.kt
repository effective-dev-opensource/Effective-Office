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
}
