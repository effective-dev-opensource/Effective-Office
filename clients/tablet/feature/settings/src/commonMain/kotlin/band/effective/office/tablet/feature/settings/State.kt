package band.effective.office.tablet.feature.settings

import band.effective.office.tablet.core.domain.model.RoomsEnum

data class State(
    val rooms: List<String>,
    val currentName: String,
    val loading: Boolean,
    val error: String,
) {
    companion object {
        val defaultState =
            State(
                rooms = RoomsEnum.entries.map { room -> room.nameRoom }.toList(),
                currentName = "",
                loading = true,
                error = "",
            )
    }
}