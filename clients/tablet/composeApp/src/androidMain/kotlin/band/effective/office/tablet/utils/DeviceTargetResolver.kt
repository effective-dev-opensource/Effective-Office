package band.effective.office.tablet.utils

import android.util.Log
import com.google.firebase.messaging.RemoteMessage

/**
 * Resolver for determining if a command should be executed on the current device.
 */
object DeviceTargetResolver {

    private const val TAG = "KIOSK_COMMAND"
    private const val KEY_DEVICE_ID = "deviceId"
    private const val KEY_DEVICE_IDS = "deviceIds"
    /**
     * Determines if the current device is a target for the given command.
     *
     * Supports both single device (`deviceId`) and multiple devices (`deviceIds`) commands.
     */
    fun shouldExecuteOnCurrentDevice(message: RemoteMessage, currentDeviceId: String): Boolean {
        val data = message.data

        return when {
            data.containsKey(KEY_DEVICE_ID) -> {
                val targetDeviceId = data[KEY_DEVICE_ID]
                val matches = targetDeviceId == currentDeviceId
                Log.d(TAG, "Single device → Target: $targetDeviceId, Current: $currentDeviceId, Execute: $matches")
                matches
            }

            data.containsKey(KEY_DEVICE_IDS) -> {
                val targetDeviceIds = parseDeviceIds(data[KEY_DEVICE_IDS])
                val matches = currentDeviceId in targetDeviceIds
                Log.d(TAG, "Multiple devices - Targets: $targetDeviceIds, Current: $currentDeviceId")
                matches
            }

            else -> {
                Log.w(TAG, "No deviceId or deviceIds specified in command")
                false
            }
        }
    }

    private fun parseDeviceIds(raw: String?): List<String> =
        raw?.split(",")
            ?.map { it.trim() }
            .orEmpty()
}