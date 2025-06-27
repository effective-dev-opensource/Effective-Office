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
class Collector<T>(defaultValue: T) {
    private data class CollectableElement<T>(val value: T, val number: Long)

    private val collection = MutableStateFlow(CollectableElement(defaultValue, 0))

    /**
     * Creates a flow that emits the collected values.
     * @param scope CoroutineScope for sharing the flow
     * @return Flow of collected values
     */
    fun flow(scope: CoroutineScope) =
        collection.map { it.value }.shareIn(
            scope = scope,
            started = SharingStarted.Lazily,
            replay = 1
        )

    /**
     * Emits a new value to the collection.
     * @param value New value to emit
     */
    fun emit(value: T) {
        collection.update { CollectableElement(value = value, number = it.number + 1) }
    }
}