package band.effective.office.tablet.feature.main.presentation.freeuproom

data class State(
    val isLoad: Boolean,
    val isSuccess: Boolean
) {
    companion object {
        val defaultState = State(
            isLoad = false,
            isSuccess = true
        )
    }
}