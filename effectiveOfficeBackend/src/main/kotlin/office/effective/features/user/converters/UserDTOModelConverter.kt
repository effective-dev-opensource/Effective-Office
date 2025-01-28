package office.effective.features.user.converters

import office.effective.common.exception.InstanceNotFoundException
import office.effective.common.utils.UuidValidator
import office.effective.dto.IntegrationDTO
import office.effective.dto.UserDTO
import office.effective.features.user.repository.UsersRepository
import office.effective.features.user.repository.UsersTagEntity
import office.effective.model.IntegrationModel
import office.effective.model.UserModel
import java.util.*
/**
 * Converters between [UserDTO] and [UserModel]
 * */
class UserDTOModelConverter(
    private val usersRepository: UsersRepository,
    private val converter: IntegrationDTOModelConverter,
    private val uuidConverter: UuidValidator
) {

    /**
     * Converts [UserDTO] to [UserModel]. Search user tags in [UserRepository] by id. Takes user's integrations from DTO.
     * @param userDTO [UserDTO] to be converted
     * @return converted [UserModel]
     *
     * @author Danil Kiselev, Daniil Zavyalov
     */
    fun dTOToModel(userDTO: UserDTO): UserModel {
        var userId: UUID? = null;
        if (userDTO.id != "null") {
            userId = uuidConverter.uuidFromString(userDTO.id)
        }
        val tag: UsersTagEntity? = userId?.let {
            try {
                usersRepository.findTagByUserOrNull(userId);
            } catch (ex: InstanceNotFoundException) {
                null
            }
        }
        val integrations: MutableSet<IntegrationModel> = mutableSetOf()
        userDTO.integrations?.forEach { integrations.add(converter.dTOToModel(it)) }
        return UserModel(
            fullName = userDTO.fullName,
            id = userId,
            tag = tag ?: UsersTagEntity(),
            active = userDTO.active,
            role = userDTO.role,
            avatarURL = userDTO.avatarUrl,
            integrations = integrations,
            email = userDTO.email
        )
    }

    /**
     * Converts [UserModel] to [UserDTO]
     * @param userModel [UserModel] to be converted
     * @return converted [UserDTO]
     *
     * @author Danil Kiselev, Daniil Zavyalov
     */
    fun modelToDTO(userModel: UserModel): UserDTO {
        val integrations: MutableList<IntegrationDTO> = mutableListOf()
        userModel.integrations?.forEach { integrations.add(converter.modelToDTO(it)) }
        return UserDTO(
            id = userModel.id.toString(),
            fullName = userModel.fullName,
            active = userModel.active,
            role = userModel.role ?: "",
            avatarUrl = userModel.avatarURL ?: "",
            integrations = integrations,
            email = userModel.email,
            tag = userModel.tag?.name ?: ""
        )
    }


}