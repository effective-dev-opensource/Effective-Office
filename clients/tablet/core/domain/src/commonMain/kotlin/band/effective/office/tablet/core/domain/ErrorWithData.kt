package band.effective.office.tablet.core.domain

data class ErrorWithData<out T>(
    val error: ErrorResponse,
    val saveData: T?
) {
    fun <NewType> map(mapper: (T) -> NewType): ErrorWithData<NewType> {
        return if (saveData != null) ErrorWithData(error, mapper(saveData))
        else ErrorWithData(error, null)
    }
}