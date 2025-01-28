package office.effective.features.user.repository

import office.effective.common.exception.*
import office.effective.features.user.converters.IntegrationModelEntityConverter
import office.effective.features.user.converters.UserModelEntityConverter
import office.effective.model.IntegrationModel
import office.effective.model.UserModel
import office.effective.model.UserTagModel
import org.ktorm.database.Database
import org.ktorm.dsl.*
import org.ktorm.entity.*
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Perform database queries with users
 * */
class UsersRepository(
    private val db: Database,
    private val converter: UserModelEntityConverter,
    private val integrationConverter: IntegrationModelEntityConverter
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Checks existence of user by id, using count
     *
     * @return [Boolean] (true - exists)
     * @param userId user id in [UUID] format
     * @author Danil Kiselev
     * */
    fun existsById(userId: UUID): Boolean {
        logger.debug("[existsById] checking whether a user with id={} exists", userId.toString())
        return true// db.users.count { it.id eq userId } > 0 TODO: FIX ME, SEMPAI
    }

    /**
     * Retrieves user model by id
     *
     * @return [UserModel] if user exists
     * @param userId user id in [UUID] format
     * @author Danil Kiselev, Daniil Zavyalov
     * */
    fun findById(userId: UUID): UserModel? {
        logger.debug("[findById] retrieving a user with id={}", userId.toString())
        val userEnt: UserEntity? = db.users.find { it.id eq userId }

        val integrations = findSetOfIntegrationsByUser(userId)

        return userEnt?.let { converter.entityToModel(userEnt, integrations) }
    }

    /**
     * Retrieves all users
     * @return [Set]<[UserModel]>
     *
     * @author Daniil Zavyalov
     * */
    fun findAll(): Set<UserModel> {
        logger.debug("[findAll] retrieving all users")
        val entities = db.users.toSet()
        if (entities.isEmpty()) return emptySet()

        val ids = mutableSetOf<UUID>()

        for (entity in entities) {
            ids.add(entity.id)
        }
        val integrations = findAllIntegrationsByUserIds(ids)

        val models = mutableSetOf<UserModel>()
        for (entity in entities) {
            models.add(converter.entityToModel(entity, integrations[entity.id]))
        }
        return models
    }

    /**
     * Used to find multiple users with one tag
     * @return [Set]<[UserModel]> - users with tag name like input
     *
     * @author Danil Kiselev, Daniil Zavyalov
     * */
    fun findByTag(tagId: UUID): Set<UserModel> {
        logger.debug("[findByTag] retrieving users with tag with id={}", tagId.toString())
        val ents = db.users.filter { Users.tagId eq tagId }.toSet()
        val models: MutableSet<UserModel> = mutableSetOf<UserModel>()
        ents.forEach {
            val user = converter.entityToModel(it, null)
            user.integrations = findSetOfIntegrationsByUser(user.id!!)
            models.add(user)

        }
        return models
    }

    /**
     * Retrieves a user model by email
     *
     * @return [UserModel]
     * @throws InstanceNotFoundException if user with the given email doesn't exist in database
     * @author Daniil Zavyalov
     */
    fun findByEmail(email: String): UserModel? {
        logger.debug("[findByEmail] retrieving a user with email {}", email)
        val entity: UserEntity? = db.users.find { it.email eq email }
        var integrations: MutableSet<IntegrationModel>? = null

        if (entity != null) {
            integrations = findSetOfIntegrationsByUser(entity.id)
        }

        return entity?.let { converter.entityToModel(entity, integrations) }
    }

    /**
     * Returns Integration entity by id
     * @return [IntegrationEntity]
     * @throws InstanceNotFoundException([IntegrationEntity], "Integration with id ${id} not found")
     *
     * @author Danil Kiselev
     * */
    private fun findIntegrationById(id: UUID): IntegrationEntity {
        logger.trace("[findIntegrationById] retrieving integrations for user with id={}", id.toString())
        return db.integrations.find { it.id eq id } ?: throw InstanceNotFoundException(
            IntegrationEntity::class, "Integration with id ${id} not found"
        )
    }

    /**
     * Returns all integrations of user (by user id)
     * @return [MutableSet]<[IntegrationModel]>
     * @throws InstanceNotFoundException([UserEntity]::class, "User $userId")
     * @param userId user id in [UUID] format
     * @author Danil Kiselev, Daniil Zavyalov
     * */
    fun findSetOfIntegrationsByUser(userId: UUID): MutableSet<IntegrationModel> {
        logger.trace("[findSetOfIntegrationsByUser] retrieving integrations for user with id={}", userId)
        if (!existsById(userId)) {
            throw InstanceNotFoundException(UserEntity::class, "User $userId")
        }
        val usersIntegrationsSet: Set<UserIntegrationEntity> =
            db.usersinegrations.filter { it.userId eq userId }.toSet()
        val modelsSet: MutableSet<IntegrationModel> = mutableSetOf()

        usersIntegrationsSet.forEach {
            val integrationEntity = findIntegrationById(it.integrationId.id!!)
            modelsSet.add(
                IntegrationModel(
                    id = integrationEntity.id!!,
                    name = integrationEntity.name,
                    valueStr = it.valueStr,
                    iconUrl = integrationEntity.iconUrl
                )
            )
        }
        return modelsSet;
    }

    /**
     * Returns a HashMap that maps user ids and their integrations
     * @return [HashMap]<[UUID], [MutableSet]<[IntegrationModel]>>
     * @throws InstanceNotFoundException if user with the given id doesn't exist in the database
     * @param ids [Collection]<[UUID]> of users id
     * @author Daniil Zavyalov, Danil Kiselev
     * */
    fun findAllIntegrationsByUserIds(ids: Collection<UUID>): HashMap<UUID, MutableSet<IntegrationModel>> {
        logger.debug(
            "[findAllIntegrationsByUserIds] retrieving integrations for users with id={}",
            ids.joinToString()
        )
        if (ids.isEmpty()) {
            return HashMap<UUID, MutableSet<IntegrationModel>>()
        }
        for (id in ids) {
            if (!existsById(id)) throw InstanceNotFoundException(UserEntity::class, "User with id $id not found")
        }
        val result = hashMapOf<UUID, MutableSet<IntegrationModel>>()
        db.from(UsersIntegrations)
            .innerJoin(right = Integrations, on = UsersIntegrations.integrationId eq Integrations.id).select()
            .where { UsersIntegrations.userId inList ids }.forEach { row ->
                val userId: UUID = row[UsersIntegrations.userId] ?: return@forEach
                val integration = integrationConverter.entityToModel(
                    Integrations.createEntity(row), row[UsersIntegrations.valueStr] ?: ""
                )
                val integrations: MutableSet<IntegrationModel> = result.getOrPut(userId) { mutableSetOf() }
                integrations.add(integration)
            }
        return result
    }

    /**
     * Returns [UserTagModel] by tag name
     * @return [UserTagModel]
     * @throws InstanceNotFoundException([UsersTagEntity]::class, "Tag with name ${tagName} not found")
     * @param tagName name of the tag
     * @author Danil Kiselev
     * */
    fun findTagByName(tagName: String): UserTagModel {
        logger.debug("[findTagByName] retrieving users with tag {}", tagName)
        val tag = db.users_tags.find { it.name eq tagName } ?: throw InstanceNotFoundException(
            UsersTagEntity::class, "Tag with name ${tagName} not found"
        )
        return UserTagModel(tag.id, tag.name)
    }

    /**
     * Updates a given user. User searched in db by id. Use the returned model for further operations.
     * @return [UserModel] updated user model
     * @throws MissingIdException if the user or integration id is null
     * @throws InstanceNotFoundException if the given user or integration don't exist
     * @param model [UserModel] with updated information
     *
     * @author Danil Kiselev, Daniil Zavyalov
     */
    fun updateUser(model: UserModel): UserModel {
        logger.debug("[updateUser] updating user with id {}", model.id)
        val userId = model.id?.let {
            if (!existsById(it)) throw InstanceNotFoundException(UserEntity::class, "User with id $it not wound", it)
            it
        } ?: throw MissingIdException("User with name ${model.fullName} doesn't have an id")

        val ent = db.users.find { it.id eq userId }
        ent?.tag = model.tag!!
        ent?.fullName = model.fullName
        ent?.active = model.active
        ent?.avatarURL = model.avatarURL
        ent?.role = model.role
        ent?.email = model.email
        ent?.flushChanges()

        val integrations: Set<IntegrationModel>? = model.integrations
        if (!integrations.isNullOrEmpty()) {
            saveIntegrations(integrations, userId)
        }
        return model
    }

    /**
     * Adds many-to-many relationship between user and its integrations
     *
     * @throws MissingIdException if the integration id is null
     * @param integrationModels [Set]<[IntegrationModel]> set of integrations to save
     * @param userId user id in [UUID] format
     * @throws InstanceNotFoundException if the given integration don't exist
     *
     * @author Daniil Zavyalov, Danil Kiselev
     */
    private fun saveIntegrations(integrationModels: Set<IntegrationModel>, userId: UUID) {
        logger.trace("[saveIntegrations] saving set of integrations for user with id={}", userId)
        db.usersinegrations.removeIf { it.userId eq userId }
        for (integration in integrationModels) {
            val integrationId: UUID = integration.id
                ?: throw MissingIdException("Integration with name ${integration.name} doesn't have an id")

            db.integrations.find { it.id eq integrationId } ?: throw InstanceNotFoundException(
                IntegrationEntity::class, "User with id $integrationId not found", integrationId
            )
            db.insert(UsersIntegrations) {
                set(it.userId, userId)
                set(it.integrationId, integrationId)
                set(it.valueStr, integration.valueStr)
            }
        }
    }

    /**
     * Returns Tag entity or null, if user with such id doesn't exist
     * @return UsersTagEntity?
     * @param userId user id in [UUID] format
     * @author Danil Kiselev
     * */
    fun findTagByUserOrNull(userId: UUID): UsersTagEntity? {
        logger.debug("[findTagByUserOrNull] retrieving tag for user with id={}", userId)
        if (!existsById(userId)) return null
        val ent = findById(userId)
        return ent?.tag
    }
}
