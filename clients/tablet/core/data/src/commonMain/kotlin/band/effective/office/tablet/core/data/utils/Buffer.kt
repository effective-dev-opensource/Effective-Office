package band.effective.office.tablet.core.data.utils

import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.ErrorResponse
import band.effective.office.tablet.core.domain.ErrorWithData
import band.effective.office.tablet.core.domain.model.RoomInfo
import kotlinx.coroutines.flow.MutableStateFlow

class Buffer {
    val state = MutableStateFlow<Either<ErrorWithData<List<RoomInfo>>, List<RoomInfo>>>(
        Either.Error(
            ErrorWithData(
                error = ErrorResponse.getResponse(400),
                saveData = emptyList()
            )
        )
    )
}