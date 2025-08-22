package band.effective.office.tablet

import android.provider.Settings
import android.util.Log
import band.effective.office.tablet.core.data.api.Collector
import band.effective.office.tablet.utils.DeviceTargetResolver
import band.effective.office.tablet.utils.KioskCommandBus
import band.effective.office.tablet.utils.KioskCommandMapper
import band.effective.office.tablet.utils.MessageType
import band.effective.office.tablet.utils.MessageValidator
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

        when (MessageType.fromString(message.data["type"])) {
            MessageType.KIOSK_TOGGLE -> handleKioskCommand(message)
            MessageType.UNKNOWN -> {
                val topic = message.from?.substringAfter("topics/")?.replace("-test", "") ?: ""
                collector.emit(topic)
            }
        }
    }

    /**
     * Processes incoming kiosk toggle commands.
     *
     * Supports both single device (deviceId) and multiple devices (deviceIds) commands.
     */
    private fun handleKioskCommand(message: RemoteMessage) {

        if (!MessageValidator.isValidKioskCommand(message)) {
            return
        }

        val isKioskModeActive = getKioskModeStatus(message) ?: return

        if (shouldExecuteCommand(message)) {
            val command = KioskCommandMapper.mapToKioskCommand(isKioskModeActive)
            kioskCommandBus.sendCommand(command, serviceScope)
        }
    }

    private fun getKioskModeStatus(message: RemoteMessage): Boolean? {
        val kioskModeValue = message.data["isKioskModeActive"]
        val isKioskModeActive = MessageValidator.validateKioskModeValue(kioskModeValue)
        return isKioskModeActive
    }

    private fun shouldExecuteCommand(message: RemoteMessage): Boolean {
        val currentDeviceId = getCurrentDeviceId()
        return DeviceTargetResolver.shouldExecuteOnCurrentDevice(message, currentDeviceId)
    }

    private fun getCurrentDeviceId(): String {
        return Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
    }
}