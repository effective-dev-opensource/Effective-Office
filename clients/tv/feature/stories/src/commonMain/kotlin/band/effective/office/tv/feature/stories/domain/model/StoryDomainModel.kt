package band.effective.office.tv.feature.stories.domain.model

import band.effective.office.tv.feature.stories.domain.model.sport.ClockifyUser
import band.effective.office.tv.feature.stories.domain.model.supernova.SupernovaScore

sealed interface StoryDomainModel {
    /**
     * Employee-related stories: birthdays, anniversaries, newcomers.
     * Contains display name/photo and timing metadata.
     */
    data class EmployeeStory(
        val name: String,
        val photoUrl: String,
        val type: EmployeeStoryType,
        val years: Int = 0,
        val months: Int = 0,
        val isIntern: Boolean = false,
    ) : StoryDomainModel

    /**
     * Duolingo leaderboard slice (XP or streak) for a set of users.
     */
    data class DuolingoStory(
        val users: List<DuolingoUser>,
        val key: DuolingoKey
    ) : StoryDomainModel

    /**
     * Sport/Clockify aggregated hours for users.
     */
    data class SportStory(
        val users: List<ClockifyUser>
    ) : StoryDomainModel

    /**
     * Supernova rating (talent scores) for teammates.
     */
    data class SupernovaStory(
        val users: List<SupernovaScore>
    ) : StoryDomainModel
}

enum class EmployeeStoryType { Birthday, Anniversary, MonthAnniversary, NewEmployee }
enum class DuolingoKey { Xp, Streak }
