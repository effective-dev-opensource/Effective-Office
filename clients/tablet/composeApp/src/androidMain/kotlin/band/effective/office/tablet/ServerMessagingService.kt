package band.effective.office.tablet

import android.util.Log
import band.effective.office.tablet.core.data.api.Collector
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ServerMessagingService() :
    FirebaseMessagingService(), KoinComponent {
    private val collector: Collector<String> by inject()

    override fun onMessageReceived(message: RemoteMessage) {
        Log.i("ReceivedMessage", message.data.toString())
        collector.emit(message.from?.substringAfter("topics/")?.replace("-test", "") ?: "")
    }
}