package band.effective.office.tablet.core.domain.model

interface SettingsStore {
    fun getString(key: String, defaultValue: String): String
    fun putString(key: String, value: String)
    fun remove(key: String)
}
