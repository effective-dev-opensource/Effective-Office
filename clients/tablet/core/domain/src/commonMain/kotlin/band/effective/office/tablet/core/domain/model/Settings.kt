package band.effective.office.tablet.core.domain.model

class SettingsManager(private val settings: SettingsStore) {

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

    private companion object {
        const val KEY_NAME_ROOM = "nameRoom"
    }
}

