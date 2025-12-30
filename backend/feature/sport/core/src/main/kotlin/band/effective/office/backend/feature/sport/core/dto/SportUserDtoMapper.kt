package band.effective.office.backend.feature.sport.core.dto

import band.effective.office.backend.feature.sport.core.domain.model.SportUser

/**
 * Mapper for converting between SportUser domain models and DTOs.
 */
object SportUserDtoMapper {

    /**
     * Converts a SportUser domain model to SportUserDTO.
     */
    private fun toSportUserDTO(sportUser: SportUser): SportUserDTO {
        return SportUserDTO(
            name = sportUser.name,
            email = sportUser.email,
            totalSeconds = sportUser.totalSeconds
        )
    }

    /**
     * Converts a list of SportUser domain models to a list of SportUserDTOs.
     */
    fun toSportUserDTOList(sportUsers: List<SportUser>): List<SportUserDTO> {
        return sportUsers.map { toSportUserDTO(it) }
    }
}
