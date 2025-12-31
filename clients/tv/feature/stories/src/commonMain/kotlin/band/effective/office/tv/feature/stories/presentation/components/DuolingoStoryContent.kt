package band.effective.office.tv.feature.stories.presentation.components

import androidx.compose.runtime.Composable
import band.effective.office.tv.feature.stories.domain.model.DuolingoKey
import band.effective.office.tv.feature.stories.domain.model.StoryDomainModel
import band.effective.office.tv.feature.stories.presentation.rating.duolingo.DuolingoRatingContent
import coil3.ImageLoader

@Composable
fun DuolingoStoryContent(
    story: StoryDomainModel.DuolingoStory,
    imageLoader: ImageLoader
) {
    DuolingoRatingContent(
        key = story.key,
        users = when (story.key) {
            DuolingoKey.Xp -> story.users.sortedByDescending { it.totalXp }
            DuolingoKey.Streak -> story.users.sortedByDescending { it.streak }
        },
        imageLoader = imageLoader
    )
}

