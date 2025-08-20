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

    /**
     * Processes incoming kiosk toggle commands.
     *
     * @param message FCM message containing the command.
     *
     * If `deviceId` is specified, only the device with matching ANDROID_ID
     * executes the command. If no `deviceId` is provided, all devices execute it.
     */
    private fun handleKioskCommand(message: RemoteMessage) {
        val targetDeviceId = message.data["deviceId"]
        val enabled = message.data["enabled"]?.toBooleanStrictOrNull()

        if (enabled == null) {
            return
        }

        val shouldExecute = if (targetDeviceId == null) {
            true
        } else {
            val currentDeviceId = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            val isForThisDevice = targetDeviceId == currentDeviceId
            Log.d("KIOSK_COMMAND", "Target: $targetDeviceId, Current: $currentDeviceId, Execute: $isForThisDevice")
            isForThisDevice
        }

        if (shouldExecute) {
            val command = if (enabled) KioskCommand.Enable else KioskCommand.Disable
            kioskCommandBus.sendCommand(command, serviceScope)
            Log.i("KIOSK_COMMAND", "Executing kiosk command: $command")
        }
    }
}