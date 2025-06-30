package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.model.SettingsManager

/**Use case for set settings*/
class SetRoomUseCase {
    /**save current room name*/
    operator fun invoke(nameRoom: String) =
        SettingsManager.current().updateSettings(nameRoom)
}