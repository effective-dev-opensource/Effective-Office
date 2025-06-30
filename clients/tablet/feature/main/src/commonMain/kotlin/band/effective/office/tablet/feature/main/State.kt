package band.effective.office.tablet.feature.main

import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class State @OptIn(ExperimentalTime::class) constructor(
    val isLoad: Boolean,
    val isData: Boolean,
    val isError: Boolean,
    val isDisconnect: Boolean,
    val updatedEvent: Any,
    val isSettings: Boolean,
    val roomList: List<Any>,
    val indexSelectRoom: Int,
    val timeToNextEvent: Int,
    val selectedDate: Instant,
) {
    companion object {
        @OptIn(ExperimentalTime::class)
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