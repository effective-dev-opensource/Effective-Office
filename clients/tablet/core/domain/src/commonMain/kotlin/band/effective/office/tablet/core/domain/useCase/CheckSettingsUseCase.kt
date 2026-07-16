package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.model.SettingsManager

/**use case for get settings values*/
class CheckSettingsUseCase(private val settingsManager: SettingsManager) {
    /**Get current room from settings*/
    operator fun invoke() =
        settingsManager.checkCurrentRoom()
}