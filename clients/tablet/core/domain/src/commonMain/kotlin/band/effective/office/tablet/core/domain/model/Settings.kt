package band.effective.office.tablet.core.domain.model
import com.russhwolf.settings.*

class SettingsManager(private val settings: Settings) {

    companion object {
        private const val KEY_NAME_ROOM = "nameRoom"

        // Singleton-like instance
        private var currentInstance: SettingsManager? = null

        fun init(settings: Settings) {
            currentInstance = SettingsManager(settings)
        }

        fun current(): SettingsManager {
            return currentInstance
                ?: error("SettingsManager not initialized. Call SettingsManager.init(settings) first.")
        }
    }

    var currentRoomName: String
        get() = settings.getString(KEY_NAME_ROOM, "")
        private set(value) {
            settings.putString(KEY_NAME_ROOM, value)
        }

    fun checkCurrentRoom(): String = currentRoomName

    fun updateSettings(newRoomName: String) {
        currentRoomName = newRoomName
    }

    fun removeRoomName() {
        settings.remove(KEY_NAME_ROOM)
    }
}

