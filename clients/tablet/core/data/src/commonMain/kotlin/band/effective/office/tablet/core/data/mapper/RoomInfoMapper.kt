package band.effective.office.tablet.core.data.mapper

import band.effective.office.tablet.core.data.dto.workspace.WorkspaceDTO
import band.effective.office.tablet.core.domain.model.RoomInfo

class RoomInfoMapper(
    private val eventInfoMapper: EventInfoMapper,
) {

    fun map(dto: WorkspaceDTO) = RoomInfo(
        name = dto.name,
        capacity = dto.utilities.firstOrNull { it.name == "place" }?.count ?: 0,
        isHaveTv = dto.utilities.any { it.name == "tv" },
        socketCount = dto.utilities.firstOrNull { it.name == "lan" }?.count ?: 0,
        eventList = dto.bookings?.map(eventInfoMapper::map) ?: emptyList(),
        currentEvent = null,
        id = dto.id
    )
}