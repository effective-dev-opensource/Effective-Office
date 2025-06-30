package band.effective.office.tablet.core.data.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class Buffer<T>(private val defaultValue: T, private val getValue: suspend () -> T) {
    private val buffer: MutableStateFlow<T> = MutableStateFlow(defaultValue)
    val bufferFlow = buffer.asStateFlow()
    suspend fun bufferedValue(): T {
        if (buffer.value == defaultValue) return freshValue()
        return buffer.value
    }

    suspend fun freshValue(): T {
        val newValue = getValue()
        buffer.emit(newValue)
        return newValue
    }

    suspend fun refresh() {
        buffer.emit(getValue())
    }

    fun update(value: T) {
        buffer.update { value }
    }
}