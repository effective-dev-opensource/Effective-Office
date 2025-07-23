package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.model.SettingsManager
import band.effective.office.tablet.core.domain.util.Loggable
import kotlinx.coroutines.CoroutineScope

/**use case for get settings values*/
class CheckSettingsUseCase : Loggable {
    override val loggableCoroutineScope: CoroutineScope? = null
    /**Get current room from settings*/
    operator fun invoke() =
        logOperation(
            operationName = "checkSettings"
        ) {
            SettingsManager.current().checkCurrentRoom()
        }
}