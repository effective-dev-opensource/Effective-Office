package band.effective.office.tv.feature.stories.domain.service

import band.effective.office.tv.feature.stories.domain.model.EmployeeStoryType
import band.effective.office.tv.feature.stories.domain.model.StoryDomainModel
import band.effective.office.tv.feature.stories.domain.model.teammates.Teammate
import band.effective.office.tv.feature.stories.domain.service.DateUtils.getMonthsFromStartDate
import band.effective.office.tv.feature.stories.domain.service.DateUtils.getYearsFromStartDate
import band.effective.office.tv.feature.stories.domain.service.DateUtils.isFirstOrThirdMonthCelebrationToday
import band.effective.office.tv.feature.stories.domain.service.DateUtils.isNewEmployeeToday
import band.effective.office.tv.feature.stories.domain.service.DateUtils.isYearCelebrationToday

/**
 * Builds celebration stories from teammates data (birthdays, anniversaries, etc.).
 */
internal object CelebrationStoryBuilder {

    /**
     * Build celebration stories for today from teammates list.
     * @param teammates List of teammates to check for celebrations
     * @return List of celebration stories (birthdays, anniversaries, new employees)
     */
    fun build(teammates: List<Teammate>): List<StoryDomainModel.EmployeeStory> =
        teammates.flatMap { teammate -> createStoriesForTeammate(teammate) }

    private fun createStoriesForTeammate(teammate: Teammate): List<StoryDomainModel.EmployeeStory> =
        buildList {
            addBirthdayIfToday(teammate)
            addWorkAnniversaryIfToday(teammate)
            addMonthMilestoneIfToday(teammate)
            addOnboardingIfRecent(teammate)
        }

    private fun MutableList<StoryDomainModel.EmployeeStory>.addBirthdayIfToday(teammate: Teammate) {
        if (teammate.nextBDay.isNotBlank() && isYearCelebrationToday(teammate.nextBDay)) {
            add(teammate.toBirthdayStory())
        }
    }

    private fun MutableList<StoryDomainModel.EmployeeStory>.addWorkAnniversaryIfToday(teammate: Teammate) {
        if (teammate.startDate.isNotBlank() && isYearCelebrationToday(teammate.startDate)) {
            val years = getYearsFromStartDate(teammate.startDate)
            if (years > 0) {
                add(teammate.toAnniversaryStory(years))
            }
        }
    }

    private fun MutableList<StoryDomainModel.EmployeeStory>.addMonthMilestoneIfToday(teammate: Teammate) {
        val isIntern = teammate.isIntern()
        if (teammate.startDate.isNotBlank() && isFirstOrThirdMonthCelebrationToday(teammate.startDate, isIntern)) {
            val months = getMonthsFromStartDate(teammate.startDate)
            add(teammate.toMonthAnniversaryStory(months))
        }
    }

    private fun MutableList<StoryDomainModel.EmployeeStory>.addOnboardingIfRecent(teammate: Teammate) {
        val isIntern = teammate.isIntern()
        if (teammate.startDate.isNotBlank() && isNewEmployeeToday(teammate.startDate, isIntern)) {
            add(teammate.toNewEmployeeStory())
        }
    }

    // Extension functions for creating stories from Teammate

    private fun Teammate.toBirthdayStory() = StoryDomainModel.EmployeeStory(
        name = name,
        photoUrl = photo,
        type = EmployeeStoryType.Birthday,
        isIntern = isIntern()
    )

    private fun Teammate.toAnniversaryStory(years: Int) = StoryDomainModel.EmployeeStory(
        name = name,
        photoUrl = photo,
        type = EmployeeStoryType.Anniversary,
        years = years,
        isIntern = isIntern()
    )

    private fun Teammate.toMonthAnniversaryStory(months: Int) = StoryDomainModel.EmployeeStory(
        name = name,
        photoUrl = photo,
        type = EmployeeStoryType.MonthAnniversary,
        years = 0,
        months = months,
        isIntern = isIntern()
    )

    private fun Teammate.toNewEmployeeStory() = StoryDomainModel.EmployeeStory(
        name = name,
        photoUrl = photo,
        type = EmployeeStoryType.NewEmployee,
        isIntern = false
    )

    private fun Teammate.isIntern(): Boolean = employment == "Intern"
}
