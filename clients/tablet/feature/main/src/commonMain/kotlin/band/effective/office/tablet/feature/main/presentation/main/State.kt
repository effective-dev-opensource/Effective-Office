package band.effective.office.tablet.feature.main.presentation.main

import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.shared.core.utils.currentLocalDateTime
import kotlinx.datetime.LocalDateTime

data class State(
    val isLoad: Boolean,
    val isData: Boolean,
    val isError: Boolean,
    val isDisconnect: Boolean,
    val updatedEvent: Any,
    val roomList: List<RoomInfo>,
    val indexSelectRoom: Int,
    val timeToNextEvent: Int,
    val selectedDate: LocalDateTime,
    val currentDate: LocalDateTime,
) {
    companion object {
        val defaultState =
            State(
                isLoad = true,
                isData = false,
                isError = false,
                isDisconnect = false,
                updatedEvent = Any(),
                roomList = listOf(),
                indexSelectRoom = 0,
                timeToNextEvent = 0,
                selectedDate = currentLocalDateTime,
                currentDate = currentLocalDateTime,
            )
    }
}