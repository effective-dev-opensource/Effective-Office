package office.effective.features.user.repository.cache

import office.effective.model.UserModel
import java.util.ArrayList
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class UsersSnapshot(users: Collection<UserModel>) {

    val usersById: MutableMap<UUID, UserModel> = ConcurrentHashMap();

    init {
        users.forEach { it ->
            it.id?.let { userId ->
                usersById.put(userId, it);
            };
        }
    }


    fun getById(id: UUID): UserModel? {
        return usersById.get(id);
    }

    fun updateUser(user: UserModel) {
        user.id?.let {
            usersById.put(it, user);
        }
    }

    fun getList(): List<UserModel> {
        return ArrayList(usersById.values);
    }

    fun getByIds(userIds: MutableSet<UUID>): Map<UUID, UserModel> {
        val res = HashMap<UUID, UserModel>();
        for (userId in userIds) {
            val usr = usersById.get(userId);
            if (usr != null) {
                res.put(userId, usr);
            }
        }
        return res;
    }

}