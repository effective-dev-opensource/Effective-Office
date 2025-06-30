package band.effective.office.tablet.core.data.utils

import band.effective.office.tablet.core.data.dto.user.UserDTO
import band.effective.office.tablet.core.data.dto.workspace.WorkspaceDTO
import band.effective.office.tablet.core.domain.model.Organizer
import band.effective.office.tablet.core.domain.model.RoomInfo

object Converter {
    fun RoomInfo.toDto(): WorkspaceDTO =
        WorkspaceDTO(
            id = id,
            name = name,
            utilities = listOf(),
            zone = null,
            tag = "meeting"
        )

    fun Organizer.toDto(): UserDTO =
        UserDTO(
            id = id,
            fullName = fullName,
            active = false,
            role = "",
            avatarUrl = "",
            integrations = null,
            email = email!!,
            tag = "employee"
        )

    fun UserDTO.toOrganizer() = Organizer(fullName = fullName, id = id, email = email)
}