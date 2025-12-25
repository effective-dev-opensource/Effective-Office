package band.effective.office.tv.autoplay.core

import band.effective.office.tv.autoplay.features.PhotosAutoplayFeature
import band.effective.office.tv.autoplay.features.StoriesAutoplayFeature
import band.effective.office.tv.core.ui.model.ContentCategory
import com.arkivanov.decompose.ComponentContext

/**
 * Responsible for caching and providing feature controllers by category.
 */
class FeatureProvider(
    private val componentContext: ComponentContext,
    private val onFinished: () -> Unit,
    private val onError: (String) -> Unit,
    private val setLoading: (Boolean) -> Unit,
) {
    private val cache = mutableMapOf<ContentCategory, AutoplayFeature>()

    /**
     * Returns feature controller for the given category.
     * Creates it once and caches for subsequent calls.
     */
    fun featureFor(category: ContentCategory?): AutoplayFeature? = when (category) {
        ContentCategory.STORIES -> cache.getOrPut(category) { buildStoriesFeature() }
        ContentCategory.PHOTOS -> cache.getOrPut(category) { buildPhotosFeature() }
        ContentCategory.EVENTS -> null
        null -> null
    }

    fun clearCache() = cache.clear()

    private fun buildStoriesFeature(): AutoplayFeature =
        StoriesAutoplayFeature(
            componentContext = componentContext,
            onFinished = onFinished,
            onError = onError,
            setLoading = setLoading,
        )

    private fun buildPhotosFeature(): AutoplayFeature =
        PhotosAutoplayFeature(
            componentContext = componentContext,
            onFinished = onFinished,
            onError = onError,
            setLoading = setLoading,
        )
}

