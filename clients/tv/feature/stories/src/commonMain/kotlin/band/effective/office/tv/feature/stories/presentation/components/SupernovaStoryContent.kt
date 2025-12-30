package band.effective.office.tv.feature.stories.presentation.components

import androidx.compose.runtime.Composable
import band.effective.office.tv.feature.stories.domain.model.StoryDomainModel
import band.effective.office.tv.feature.stories.presentation.rating.supernova.SupernovaRatingContent
import coil3.ImageLoader

@Composable
fun SupernovaStoryContent(
    story: StoryDomainModel.SupernovaStory,
    imageLoader: ImageLoader
) {
    SupernovaRatingContent(
        users = story.users.sortedByDescending { it.score },
        imageLoader = imageLoader
    )
}

