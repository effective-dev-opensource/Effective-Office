package band.effective.office.tablet

import android.provider.Settings
import android.util.Log
import band.effective.office.tablet.core.data.api.Collector
import band.effective.office.tablet.utils.KioskCommand
import band.effective.office.tablet.utils.KioskCommandBus
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ServerMessagingService() :
    FirebaseMessagingService(), KoinComponent {
    private val collector: Collector<String> by inject()
    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private val kioskCommandBus = KioskCommandBus.getInstance()

    override fun onMessageReceived(message: RemoteMessage) {
        Log.i("FCM_MESSAGE", "From: ${message.from}, Data: ${message.data}")

        if (message.data["type"] == "KIOSK_TOGGLE") {
            handleKioskCommand(message)
        } else {
            val topic = message.from?.substringAfter("topics/")?.replace("-test", "") ?: ""
            collector.emit(topic)
        }
    }

    private fun handleKioskCommand(message: RemoteMessage) {
        val deviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val targetDeviceId = message.data["deviceId"]
        val enabled = message.data["enabled"]?.toBooleanStrictOrNull()

        if (enabled == null) {
            return
        }

        if (targetDeviceId == null || targetDeviceId == deviceId) {
            val command = if (enabled) KioskCommand.Enable else KioskCommand.Disable
            kioskCommandBus.sendCommand(command, serviceScope)
        } else {
            Log.d("KIOSK_COMMAND", "Command ignored - not for this device")
        }
    }
}