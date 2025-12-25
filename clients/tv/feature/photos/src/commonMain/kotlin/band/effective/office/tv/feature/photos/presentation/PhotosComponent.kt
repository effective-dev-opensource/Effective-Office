package band.effective.office.tv.feature.photos.presentation

import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.collectToEitherList
import band.effective.office.shared.core.utils.componentCoroutineScope
import band.effective.office.tv.feature.photos.domain.model.Photo
import band.effective.office.tv.feature.photos.domain.repository.PhotosRepository
import com.arkivanov.decompose.ComponentContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import io.github.aakira.napier.Napier

const val PHOTO_DURATION_MS = 10_000L // 10 sec

/**
 * Decompose component responsible for managing the photos slideshow.
 * Notifies parent via [onFinished] when slideshow ends.
 */
class PhotosComponent(
    componentContext: ComponentContext,
    private val onFinished: () -> Unit,
    private val onError: (String) -> Unit = {},
    private val setLoading: (Boolean) -> Unit = {},
) : ComponentContext by componentContext, KoinComponent {

    private val repository: PhotosRepository by inject()
    private val scope = componentCoroutineScope()

    private val _state = MutableStateFlow(PhotosState.initial)
    val state: StateFlow<PhotosState> = _state.asStateFlow()

    private var autoAdvanceJob: Job? = null
    private var loadJob: Job? = null

    init {
        loadPhotos()
    }

    fun restartFromStart() {
        if (_state.value.hasItems) {
            updateIndex(0)
            scheduleNextIfPlaying()
        }
    }

    /** Jump directly to the last photo */
    fun moveToLastFromStart() {
        val items = _state.value.items
        if (items.isNotEmpty()) {
            updateIndex(items.lastIndex)
            scheduleNextIfPlaying()
        }
    }

    fun onHidden() {
        setPlaying(false)
        stopAutoAdvance()
    }

    fun onIntent(intent: PhotosIntent) {
        when (intent) {
            PhotosIntent.Next -> moveNext()
            PhotosIntent.Previous -> movePrevious()
            PhotosIntent.Retry -> loadPhotos()
            is PhotosIntent.SetPlaying -> setPlaying(intent.isPlaying)
            is PhotosIntent.RemoveFailedPhoto -> removePhoto(intent.errorUrl)
        }
    }

    private fun loadPhotos() {
        loadJob?.cancel()
        loadJob = scope.launch {

            _state.update { it.copy(isLoading = true, error = null) }
            setLoading(true)

            when (val result = repository.getPhotos().collectToEitherList()) {
                is Either.Success -> handleLoadSuccess(result.data)
                is Either.Error -> handleLoadError(result.error.description)
            }
        }
    }

    private fun handleLoadSuccess(photos: List<Photo>) {
        Napier.d("Loaded ${photos.size} items")

        _state.update {
            it.copy(
                items = photos,
                currentIndex = 0,
                isLoading = false,
                error = null
            )
        }
        setLoading(false)

        if (_state.value.hasItems) {
            scheduleNextIfPlaying() // Auto-start if playback is enabled
        } else {
            Napier.d("No items, finishing")
            onFinished()
        }
    }

    private fun handleLoadError(message: String) {
        Napier.e("Load error - $message")

        _state.update {
            it.copy(
                isLoading = false,
                error = message
            )
        }
        setLoading(false)
        stopAutoAdvance()
        onError(message)
    }

    /**
     * Enable or disable auto-advance.
     * When enabled and photos are available, starts timer for next photo.
     */
    private fun setPlaying(isPlaying: Boolean) {
        _state.update { it.copy(isPlaying = isPlaying) }

        if (isPlaying && _state.value.canPlay) {
            scheduleNext()
        } else {
            stopAutoAdvance()
        }
    }

    /** Helper: start auto-advance only if playback is active */
    private fun scheduleNextIfPlaying() {
        if (_state.value.isPlaying && _state.value.canPlay) {
            scheduleNext()
        }
    }

    /** Schedule transition to the next photo after delay */
    private fun scheduleNext(delayMs: Long = PHOTO_DURATION_MS) {
        if (_state.value.items.isEmpty()) return

        autoAdvanceJob?.cancel()
        autoAdvanceJob = scope.launch {
            delay(delayMs)
            moveNext()
        }
    }

    private fun stopAutoAdvance() {
        autoAdvanceJob?.cancel()
        autoAdvanceJob = null
    }

    // Navigation
    private fun moveNext() {
        val current = _state.value
        if (current.items.isEmpty()) return

        val nextIndex = current.currentIndex + 1

        if (nextIndex >= current.items.size) {
            Napier.d("Photos: Reached end, finishing")
            stopAutoAdvance()
            onFinished()
        } else {
            updateIndex(nextIndex)
            scheduleNextIfPlaying()
        }
    }

    private fun movePrevious() {
        val current = _state.value
        if (current.items.isEmpty()) return

        val prevIndex = (current.currentIndex - 1).coerceAtLeast(0)
        updateIndex(prevIndex)
        scheduleNextIfPlaying()
    }

    private fun updateIndex(newIndex: Int) {
        _state.update { it.copy(currentIndex = newIndex) }
        Napier.v("Index updated to $newIndex")
    }

    /**
     * Remove a photo that failed to load from the list.
     * Adjusts current index if necessary and continues playback if items remain.
     */
    private fun removePhoto(errorUrl: String) {
        _state.update { current ->
            val newItems = current.items.filter { it.url != errorUrl }

            if (newItems.isEmpty()) {
                return@update current.copy(items = emptyList())
            }

            val wasCurrentRemoved = current.currentItem?.url == errorUrl
            val newIndex = if (wasCurrentRemoved) {
                current.currentIndex.coerceAtMost(newItems.lastIndex)
            } else {
                current.currentIndex.coerceAtMost(newItems.lastIndex)
            }

            current.copy(items = newItems, currentIndex = newIndex)
        }

        Napier.w("Removed failed photo $errorUrl, remaining=${_state.value.items.size}")

        if (_state.value.hasItems) {
            scheduleNextIfPlaying()
        } else {
            Napier.d("No items left, finishing")
            stopAutoAdvance()
            onFinished()
        }
    }
}
