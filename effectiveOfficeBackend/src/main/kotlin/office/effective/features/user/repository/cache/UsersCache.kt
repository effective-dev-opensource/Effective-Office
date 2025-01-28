package office.effective.features.user.repository.cache

import office.effective.features.user.repository.UsersRepository
import office.effective.model.IntegrationModel
import office.effective.model.UserModel
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.collections.HashSet

class UsersCache(val usersRepository: UsersRepository) {

    @Volatile
    var snapshot: UsersSnapshot = UsersSnapshot(setOf());

    val scheduler: ScheduledExecutorService = Executors.newScheduledThreadPool(1);


    init {
        initSnapshot();
        scheduler.scheduleWithFixedDelay({ initSnapshot() }, 1, 1, TimeUnit.MINUTES);
    }


    fun invalidate() {
        initSnapshot();
    }

    fun invalidateUser(id: UUID?) {
        if (id == null) {
            return;
        }
        usersRepository.findById(id)?.let {
            snapshot.updateUser(it);
        }
    }

    fun stop() {
        scheduler.shutdown();
        try {
            scheduler.awaitTermination(1, TimeUnit.MINUTES);
        } catch (e: InterruptedException) {
            scheduler.shutdownNow();
        }
    }


    private fun initSnapshot() {
        this.snapshot = UsersSnapshot(usersRepository.findAll());
    }

    fun findByTagId(id: UUID?): Set<UserModel> {
        if (id == null) {
            return emptySet();
        }
        return snapshot.getList().filter { Objects.equals(it.tag?.id, id) }.toSet();
    }


    fun getAll(): Set<UserModel> {
        return snapshot.getList().toSet();
    }

    fun getById(id: UUID?): UserModel? {
        if (id == null) {
            return null;
        }
        return snapshot.getById(id);
    }

    fun getByEmail(emailStr: String): UserModel? {
        return snapshot.getList().find { Objects.equals(emailStr, it.email) };
    }

    fun findAllIntegrationsByUserIds(userIds: MutableSet<UUID>): HashMap<UUID, MutableSet<IntegrationModel>> {
        val userModels: Map<UUID, UserModel> = snapshot.getByIds(userIds);
        val res = HashMap<UUID, MutableSet<IntegrationModel>>();
        for (userId in userIds) {
            val usr = userModels.get(userId);
            if (usr?.integrations != null) {
                res.put(userId, HashSet(usr.integrations!!))
            } else {
                res.put(userId, HashSet());
            }
        }
        return res;
    }

    fun existsById(userId: UUID): Boolean {
        return snapshot.getById(userId)!=null;
    }

    fun findSetOfIntegrationsByUser(userId: UUID): Set<IntegrationModel> {
        var res =  snapshot.getById(userId)?.integrations;
        return  HashSet(res ?: emptySet());
    }

}