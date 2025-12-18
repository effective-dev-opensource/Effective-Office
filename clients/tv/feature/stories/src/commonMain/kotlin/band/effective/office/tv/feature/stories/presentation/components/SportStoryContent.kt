package band.effective.office.tv.feature.stories.presentation.components

import androidx.compose.runtime.Composable
import band.effective.office.tv.feature.stories.domain.model.StoryDomainModel
import band.effective.office.tv.feature.stories.presentation.rating.sport.SportRatingContent
import coil3.ImageLoader

@Composable
fun SportStoryContent(
    story: StoryDomainModel.SportStory,
    imageLoader: ImageLoader
) {
    SportRatingContent(
        users = story.users.sortedByDescending { it.totalSeconds },
        imageLoader = imageLoader
    )
}

