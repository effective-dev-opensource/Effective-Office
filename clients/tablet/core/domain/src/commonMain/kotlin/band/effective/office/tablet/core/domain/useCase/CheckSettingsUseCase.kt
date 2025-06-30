package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.model.SettingsManager

/**use case for get settings values*/
class CheckSettingsUseCase {
    /**Get current room from settings*/
    operator fun invoke() =
        SettingsManager.current().checkCurrentRoom()
}