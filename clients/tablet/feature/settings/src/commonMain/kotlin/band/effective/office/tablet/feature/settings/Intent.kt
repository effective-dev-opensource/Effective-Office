package band.effective.office.tablet.feature.settings

sealed interface Intent {
    object OnExitApp : Intent
    data class ChangeCurrentNameRoom(val nameRoom: String) : Intent
    object SaveData : Intent
}