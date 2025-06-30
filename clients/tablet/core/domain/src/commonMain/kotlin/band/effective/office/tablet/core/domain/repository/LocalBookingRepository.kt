package band.effective.office.tablet.core.domain.repository

import band.effective.office.tablet.core.domain.Either
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.model.RoomInfo

interface LocalBookingRepository : BookingRepository {
    fun updateRoomsInfo(either: Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>)
}