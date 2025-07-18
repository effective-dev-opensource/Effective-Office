package band.effective.office.tablet.core.data.api

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update

/**
 * Utility class for collecting and sharing data updates.
 * @param T Type of data to collect
 * @param defaultValue Default value for the data
 */
// TODO rename
class Collector<T>(defaultValue: T) {
    private data class CollectableElement<T>(val value: T, val number: Long)

    private val collection = MutableStateFlow(CollectableElement(defaultValue, 0))

    fun flow() =
        collection.map { it.value }

    /**
     * Emits a new value to the collection.
     * @param value New value to emit
     */
    fun emit(value: T) {
        collection.update { CollectableElement(value = value, number = it.number + 1) }
    }
}