package band.effective.office.backend.feature.teammates.core.dto

import band.effective.office.backend.feature.teammates.core.domain.model.Teammate
import band.effective.office.backend.feature.teammates.core.domain.model.TeammateScore

object TeammateDtoMapper {

    private fun toTeammateDTO(teammate: Teammate): TeammateDTO {
        return TeammateDTO(
            id = teammate.id,
            name = teammate.name,
            positions = teammate.positions,
            employment = teammate.employment,
            startDate = teammate.startDate,
            nextBDay = teammate.nextBDay,
            duolingo = teammate.duolingo,
            photo = teammate.photo,
            status = teammate.status,
            isActive = teammate.isActive()
        )
    }

    fun toTeammateDTOList(teammates: List<Teammate>): List<TeammateDTO> {
        return teammates.map { toTeammateDTO(it) }
    }

    private fun toScoreDTO(score: TeammateScore): TeammateScoreDTO {
        return TeammateScoreDTO(
            id = score.id,
            score = score.score
        )
    }

    fun toScoreDTOList(scores: List<TeammateScore>): List<TeammateScoreDTO> {
        return scores.map { toScoreDTO(it) }
    }
}


