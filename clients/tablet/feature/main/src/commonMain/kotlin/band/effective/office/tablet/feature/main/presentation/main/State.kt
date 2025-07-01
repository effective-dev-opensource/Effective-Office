package band.effective.office.tablet.feature.main.presentation.main

import band.effective.office.tablet.core.domain.model.RoomInfo
import kotlin.time.Clock
import kotlin.time.Instant

data class State(
    val isLoad: Boolean,
    val isData: Boolean,
    val isError: Boolean,
    val isDisconnect: Boolean,
    val updatedEvent: Any,
    val isSettings: Boolean,
    val roomList: List<RoomInfo>,
    val indexSelectRoom: Int,
    val timeToNextEvent: Int,
    val selectedDate: Instant,
) {
    companion object {
        val defaultState =
            State(
                isLoad = true,
                isData = false,
                isError = false,
                isDisconnect = false,
                isSettings = false,
                updatedEvent = Any(),
                roomList = listOf(),
                indexSelectRoom = 0,
                timeToNextEvent = 0,
                selectedDate = Clock.System.now(),
            )
    }
}