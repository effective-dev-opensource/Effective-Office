package band.effective.office.tablet.core.domain.useCase

import band.effective.office.tablet.core.domain.model.EventInfo
import band.effective.office.tablet.core.domain.model.RoomInfo
import band.effective.office.tablet.core.domain.util.Loggable
import band.effective.office.tablet.core.domain.util.cropSeconds
import kotlinx.coroutines.CoroutineScope

/**Use case for checking booking room opportunity*/
class CheckBookingUseCase(
    private val roomInfoUseCase: RoomInfoUseCase
) : Loggable {
    override val loggableCoroutineScope: CoroutineScope? = null
    /** get event blocking room for booking
     * @param event info about event
     * @param room room name
     * @return Event busy with room booking, if room free, return null*/
    suspend operator fun invoke(event: EventInfo, room: String) =
        logSuspendOperation(
            operationName = "checkBooking",
            params = "room=$room, eventId=${event.id}",
            resultMessage = { conflicts -> "conflicts=${conflicts.size}" }
        ) {
            busyEvents(event, room)
        }

    /** get events blocking room for booking
     * @param event info about event
     * @param room room name
     * @return List events busy with room booking, if room's free then empty list will be returned*/
    suspend fun busyEvents(event: EventInfo, room: String): List<EventInfo> =
        logSuspendOperation(
            operationName = "fetchBusyEvents",
            params = "room=$room, eventId=${event.id}"
        ) {
            val eventList = eventList(room)
            eventList.getBusy(event)
        }

    private suspend fun eventList(room: String): List<EventInfo> {
        val roomInfo = roomInfoUseCase.getRoom(room)
        return roomInfo?.getAllEvents() ?: emptyList()
    }

    /**
     * @return True, if the moment belongs to the time interval between event start and end*/
    private fun EventInfo.collidesWith(event: EventInfo) =
        this.startTime.cropSeconds() < event.finishTime.cropSeconds()
                && this.finishTime.cropSeconds() > event.startTime.cropSeconds()

    private fun RoomInfo.getAllEvents(): List<EventInfo> =
        if (currentEvent != null) eventList + currentEvent else eventList

    private fun List<EventInfo>.getBusy(event: EventInfo) =
        filter { it.collidesWith(event) }
}