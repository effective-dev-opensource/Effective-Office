package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.model.SettingsManager

/**Use case for set settings*/
class SetRoomUseCase(private val settingsManager: SettingsManager) {
    /**save current room name*/
    operator fun invoke(nameRoom: String) =
        settingsManager.updateSettings(nameRoom)
}