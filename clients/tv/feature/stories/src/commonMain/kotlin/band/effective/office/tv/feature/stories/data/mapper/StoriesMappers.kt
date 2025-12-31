package band.effective.office.tv.feature.stories.data.mapper

import band.effective.office.tv.feature.stories.data.dto.*
import band.effective.office.tv.feature.stories.domain.model.DuolingoUser
import band.effective.office.tv.feature.stories.domain.model.teammates.Teammate
import band.effective.office.tv.feature.stories.domain.model.sport.ClockifyUser
import band.effective.office.tv.feature.stories.domain.model.supernova.SupernovaScore

object TeammateMapper {
    fun toDomain(dto: TeammateDTO): Teammate = Teammate(
        id = dto.id,
        name = dto.name,
        startDate = dto.startDate,
        nextBDay = dto.nextBDay,
        photo = dto.photo.orEmpty(),
        employment = dto.employment,
        duolingo = dto.duolingo,
        email = dto.email
    )
}

object DuolingoMapper {
    fun map(dto: DuolingoUserDTO): DuolingoUser = DuolingoUser(
        username = dto.username.orEmpty(),
        name = dto.name,
        streak = dto.streak ?: 0,
        totalXp = dto.totalXp ?: 0,
        countryLang = dto.courses?.mapNotNull { it.learningLanguage }.orEmpty(),
        photo = dto.picture
    )
}

object SportMapper {
    fun toDomain(dto: SportUserDTO): ClockifyUser = ClockifyUser(
        name = dto.name,
        email = dto.email,
        totalSeconds = dto.totalSeconds,
        photo = null
    )
}

object SupernovaMapper {
    fun toDomain(dto: SupernovaTalentDTO): SupernovaScore = SupernovaScore(
        id = dto.id,
        score = dto.score
    )
}


