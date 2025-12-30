package band.effective.office.tv.feature.stories.domain.service

import band.effective.office.shared.core.domain.Either
import band.effective.office.shared.core.domain.ErrorResponse
import band.effective.office.shared.core.domain.fold
import band.effective.office.shared.core.domain.collectToEitherList
import band.effective.office.tv.feature.stories.domain.model.DuolingoKey
import band.effective.office.tv.feature.stories.domain.model.DuolingoUser
import band.effective.office.tv.feature.stories.domain.model.StoryDomainModel
import band.effective.office.tv.feature.stories.domain.model.sport.ClockifyUser
import band.effective.office.tv.feature.stories.domain.model.supernova.SupernovaScore
import band.effective.office.tv.feature.stories.domain.model.teammates.Teammate
import band.effective.office.tv.feature.stories.domain.repository.ClockifyRepository
import band.effective.office.tv.feature.stories.domain.repository.DuolingoRepository
import band.effective.office.tv.feature.stories.domain.repository.NotionRepository
import band.effective.office.tv.feature.stories.domain.repository.SupernovaRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf

/**
 * Service that combines data from multiple repositories to create stories.
 * Aggregates data from Notion, Duolingo, Clockify, and Supernova.
 */
data class StoriesPayload(
    val stories: List<StoryDomainModel>,
    val warnings: List<String>
)

class StoriesDataProvider(
    private val notionRepository: NotionRepository,
    private val duolingoRepository: DuolingoRepository,
    private val clockifyRepository: ClockifyRepository,
    private val supernovaRepository: SupernovaRepository,
) {

    /**
     * Load all stories from various data sources.
     * @return Flow with list of stories or error message
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    suspend fun loadStories(): Flow<Either<String, StoriesPayload>> {
        val teammatesResult = notionRepository.getTeammates().collectToEitherList()

        return when (teammatesResult) {
            is Either.Error -> flowOf(Either.Error(teammatesResult.error.description))
            is Either.Success -> {
                val teammates = teammatesResult.data
                teammates.chunked(100)
                    .asFlow()
                    .flatMapConcat { chunk -> combineAllStories(chunk) }
            }
        }
    }

    private suspend fun combineAllStories(
        teammates: List<Teammate>
    ): Flow<Either<String, StoriesPayload>> {
        val duolingoResult = duolingoRepository.getUsers(teammates).collectToEitherList()
        val clockifyResult = clockifyRepository.getTimeEntries().collectToEitherList()
        val supernovaResult = supernovaRepository.getTeammateScores().collectToEitherList()

        return flowOf(
            buildStoriesPayload(teammates, duolingoResult, clockifyResult, supernovaResult)
        )
    }

    private fun buildStoriesPayload(
        teammates: List<Teammate>,
        duolingoResult: Either<ErrorResponse, List<DuolingoUser>>,
        clockifyResult: Either<ErrorResponse, List<ClockifyUser>>,
        supernovaResult: Either<ErrorResponse, List<SupernovaScore>>
    ): Either<String, StoriesPayload> {
        val stories = mutableListOf<StoryDomainModel>()
        var lastError: String? = null
        val warnings = mutableListOf<String>()

        stories += CelebrationStoryBuilder.build(teammates)

        duolingoResult.fold(
            onSuccess = { users ->
                stories += createDuolingoStories(users, teammates)
            },
            onError = {
                lastError = it.description
                warnings += it.description
            }
        )

        clockifyResult.fold(
            onSuccess = { users ->
                val aggregated = users
                    .groupBy { user ->
                        val emailKey = user.email?.trim()?.lowercase().orEmpty()
                        if (emailKey.isNotEmpty()) emailKey
                        else formatName(user.name).lowercase()
                    }
                    .values
                    .map { group ->
                        val sample = group.first()
                        sample.copy(totalSeconds = group.sumOf { it.totalSeconds })
                    }

                val enriched = aggregated.map { user ->
                    val formattedName = formatName(user.name)
                    val teammate = teammates.firstOrNull { teammate ->
                        teammate.email?.equals(user.email, ignoreCase = true) == true ||
                            teammate.name.equals(user.name, ignoreCase = true) ||
                            teammate.name.equals(formattedName, ignoreCase = true)
                    }
                    user.copy(
                        name = teammate?.name ?: formattedName,
                        photo = teammate?.photo
                    )
                }
                stories += StoryDomainModel.SportStory(enriched)
            },
            onError = {
                lastError = it.description
                warnings += it.description
            }
        )

        supernovaResult.fold(
            onSuccess = { talents ->
                val enriched = talents.map { talent ->
                    val teammate = teammates.firstOrNull { it.id == talent.id }
                    talent.copy(name = teammate?.name, photo = teammate?.photo)
                }
                stories += StoryDomainModel.SupernovaStory(enriched)
            },
            onError = {
                lastError = it.description
                warnings += it.description
            }
        )

        return if (stories.isEmpty()) {
            Either.Error(lastError ?: "No data available")
        } else {
            Either.Success(
                StoriesPayload(
                    stories = stories.toList(),
                    warnings = warnings.toList()
                )
            )
        }
    }

    private fun formatName(raw: String?): String {
        if (raw.isNullOrBlank()) return raw.orEmpty()
        val separators = charArrayOf('.', '_')
        val parts = raw.split(*separators).filter { it.isNotBlank() }
        if (parts.isEmpty()) return raw
        return parts.joinToString(" ") { part ->
            part.lowercase().replaceFirstChar { it.titlecase() }
        }
    }

    private fun createDuolingoStories(
        users: List<DuolingoUser>,
        teammates: List<Teammate>
    ): List<StoryDomainModel> {
        val enrichedUsers = users.map { user ->
            val teammate = teammates.firstOrNull { it.duolingo == user.username }
            val displayName = teammate?.name ?: user.name ?: user.username
            user.copy(
                photo = teammate?.photo,
                name = displayName
            )
        }

        return buildList {
            add(
                StoryDomainModel.DuolingoStory(
                    users = enrichedUsers.sortedByDescending { it.totalXp },
                    key = DuolingoKey.Xp
                )
            )
            val usersWithStreak = enrichedUsers.filter { it.streak > 0 }
            if (usersWithStreak.isNotEmpty()) {
                add(
                    StoryDomainModel.DuolingoStory(
                        users = usersWithStreak.sortedByDescending { it.streak },
                        key = DuolingoKey.Streak
                    )
                )
            }
        }
    }
}
