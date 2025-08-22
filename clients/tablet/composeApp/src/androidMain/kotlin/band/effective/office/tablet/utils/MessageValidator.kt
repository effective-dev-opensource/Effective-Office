package band.effective.office.tablet.utils

import com.google.firebase.messaging.RemoteMessage

/**
 * Validator for checking the integrity of incoming messages.
 */
object MessageValidator {
    /**
     * Checks if the message contains valid kiosk command data.
     */
    fun isValidKioskCommand(message: RemoteMessage): Boolean {
        return message.data.containsKey("isKioskModeActive") &&
                (message.data.containsKey("deviceId") || message.data.containsKey("deviceIds"))
    }

    /**
     * Validates and converts the kiosk mode value from a string to a boolean.
     */
    fun validateKioskModeValue(value: String?): Boolean? {
        return try {
            value?.toBooleanStrictOrNull()
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}