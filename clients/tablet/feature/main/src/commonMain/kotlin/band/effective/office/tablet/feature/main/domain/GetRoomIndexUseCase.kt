package band.effective.office.tablet.feature.main.domain

import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.useCase.CheckSettingsUseCase

class GetRoomIndexUseCase(
    private val checkSettingsUseCase: CheckSettingsUseCase,
) {
    operator fun invoke(rooms: List<RoomInfo>) = rooms.indexOfFirst { it.name == checkSettingsUseCase() }.run {
        if (this < 0) 0 else this
    }
}