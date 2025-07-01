package band.effective.office.tablet.feature.main.presentation.main.navigation

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import kotlinx.serialization.Serializable

@Serializable
sealed interface ModalWindowsConfig {

    @Serializable
    data class UpdateEvent(
        val event: EventInfo,
        val room: String
    ) : ModalWindowsConfig

    @Serializable
    data class FreeRoom(val event: EventInfo) : ModalWindowsConfig

    @Serializable
    data class FastEvent(
        val minEventDuration: Int,
        val selectedRoom: RoomInfo,
        val rooms: List<RoomInfo>
    ) : ModalWindowsConfig
}