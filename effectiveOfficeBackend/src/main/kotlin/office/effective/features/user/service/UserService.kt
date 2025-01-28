package office.effective.features.user.service

import office.effective.features.user.repository.UsersRepository
import office.effective.features.user.repository.cache.UsersCache
import office.effective.model.UserModel
import office.effective.serviceapi.IUserService
import java.util.*

/**
 * Class that provides methods to manipulate [UserModel] objects
 * */
class UserService(private val cache: UsersCache, private val repository: UsersRepository) : IUserService {

    override fun getUsersByTag(tagStr: String): Set<UserModel> {
        return cache.findByTagId(repository.findTagByName(tagStr).id)
    }

    /**
     * Retrieves all users
     * @return [Set]<[UserModel]>
     *
     * @author Daniil Zavyalov
     * */
    override fun getAll(): Set<UserModel> {
        return cache.getAll()
    }

    override fun getUserById(userIdStr: String): UserModel? {
        return cache.getById(UUID.fromString(userIdStr));
    }

    /**
     * Updates a given user. Use the returned model for further operations
     *
     * @param user User's model
     * @return [UserModel]
     * @author Kiselev Danil
     */
    override fun updateUser(user: UserModel): UserModel {
        val res = repository.updateUser(user)
        cache.invalidateUser(res.id);
        return res;
    }

    /**
     * Return user with specified email
     *
     * @param emailStr user email
     * @return [UserModel]
     * @author Kiselev Danil
     * */
    override fun getUserByEmail(emailStr: String): UserModel? {
        return cache.getByEmail(emailStr)
    }
}