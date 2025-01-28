package office.effective.features.booking.service

import office.effective.common.exception.InstanceNotFoundException
import office.effective.common.exception.MissingIdException
import office.effective.features.booking.repository.BookingCalendarRepository
import office.effective.features.booking.repository.BookingWorkspaceRepository
import office.effective.features.booking.repository.WorkspaceBookingEntity
import office.effective.features.user.repository.UserEntity
import office.effective.features.user.repository.cache.UsersCache
import office.effective.features.workspace.repository.WorkspaceRepository
import office.effective.model.*
import office.effective.serviceapi.IBookingService
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * Class that implements the [IBookingService] methods
 *
 * Uses [BookingCalendarRepository] for operations with meeting rooms.
 * Uses [BookingWorkspaceRepository] for operations with workspaces.
 */
class BookingService(
    private val bookingRepository: BookingCalendarRepository,
    private val bookingWorkspaceRepository: BookingWorkspaceRepository,
    private val usersCache: UsersCache,
    private val workspaceRepository: WorkspaceRepository,
) : IBookingService {
    private val logger = LoggerFactory.getLogger(BookingService::class.java)

    /**
     * Returns whether a booking with the given id exists
     *
     * @param id booking id
     * @return true if booking exists
     * @author Daniil Zavyalov
     */
    override fun existsById(id: String): Boolean {
        return bookingRepository.existsById(id) || bookingWorkspaceRepository.existsById(id)
    }

    /**
     * Deletes the booking with the given id
     *
     * @param id booking id
     * @author Daniil Zavyalov
     */
    override fun deleteById(id: String) {
        bookingRepository.deleteById(id)
        bookingWorkspaceRepository.deleteById(id)
    }

    /**
     * Retrieves a booking model by its id
     *
     * @param id - booking id
     * @return [Booking] with the given [id] or null if workspace with the given id doesn't exist
     * @author Daniil Zavyalov
     */
    override fun findById(id: String): Booking? {
        val booking = bookingRepository.findById(id)
            ?: bookingWorkspaceRepository.findById(id)
            ?: return null
        val userIds = mutableSetOf<UUID>()
        for (participant in booking.participants) {
            participant.id?.let { userIds.add(it) }
        }
        booking.owner.id?.let { userIds.add(it) }
        val integrations = usersCache.findAllIntegrationsByUserIds(userIds)
        booking.workspace.utilities = findUtilities(booking.workspace)
        booking.owner.integrations = integrations[booking.owner.id] ?: setOf()
        for (participant in booking.participants) {
            participant.integrations = integrations[participant.id] ?: setOf()
        }
        return booking
    }

    /**
     * Returns all bookings. Bookings can be filtered by owner and workspace id
     *
     * @param userId use to filter by booking owner id
     * @param workspaceId use to filter by booking workspace id
     * @param bookingRangeTo upper bound (exclusive) for a beginBooking to filter by. Optional.
     * @param bookingRangeFrom lower bound (exclusive) for a endBooking to filter by.
     * @throws InstanceNotFoundException if [UserModel] or [Workspace] with the given id doesn't exist in database
     * @author Daniil Zavyalov
     */
    override fun findAll(
        userId: UUID?,
        workspaceId: UUID?,
        bookingRangeTo: Long?,
        bookingRangeFrom: Long
    ): List<Booking> {
        val bookingList = when {
            userId != null && workspaceId != null -> {
                val workspace = workspaceRepository.findById(workspaceId)
                    ?: throw InstanceNotFoundException(
                        UserEntity::class, "User with id $workspaceId not found", workspaceId
                    )
                if (!usersCache.existsById(userId)) throw InstanceNotFoundException(
                    UserEntity::class, "User with id $userId not found", userId
                )

                if (workspace.tag == "meeting") {
                    bookingRepository.findAllByOwnerAndWorkspaceId(
                        userId,
                        workspaceId,
                        bookingRangeFrom,
                        bookingRangeTo
                    )
                } else {
                    bookingWorkspaceRepository.findAllByOwnerAndWorkspaceId(
                        userId,
                        workspaceId,
                        bookingRangeFrom,
                        bookingRangeTo
                    )
                }
            }

            userId != null -> {
                if (!usersCache.existsById(userId)) throw InstanceNotFoundException(
                    UserEntity::class, "User with id $userId not found", userId
                )

                val bookings = bookingRepository.findAllByOwnerId(
                        userId,
                        bookingRangeFrom,
                        bookingRangeTo
                    ).toMutableList()
                bookings.addAll(
                    bookingWorkspaceRepository.findAllByOwnerId(
                        userId,
                        bookingRangeFrom,
                        bookingRangeTo
                    )
                )
                bookings
            }

            workspaceId != null -> {
                val workspace = workspaceRepository.findById(workspaceId)
                    ?: throw InstanceNotFoundException(
                        UserEntity::class, "Workspace with id $workspaceId not found", workspaceId
                    )

                if (workspace.tag == "meeting") {
                    bookingRepository.findAllByWorkspaceId(
                        workspaceId,
                        bookingRangeFrom,
                        bookingRangeTo
                    )
                } else {
                    bookingWorkspaceRepository.findAllByWorkspaceId(
                        workspaceId,
                        bookingRangeFrom,
                        bookingRangeTo
                    )
                }

            }

            else -> {
                val bookings = bookingRepository.findAll(bookingRangeFrom, bookingRangeTo).toMutableList()
                bookings.addAll(bookingWorkspaceRepository.findAll(bookingRangeFrom, bookingRangeTo))
                bookings
            }
        }
        if (bookingList.isEmpty()) return bookingList
        return findIntegrationsAndUtilities(bookingList)
    }

    /**
     * Adds integrations and utilities to related user and workspace models.
     * Use the returned booking list for further operations
     *
     * @param bookingList bookings for which user integrations and workspace utilities need to be found and added.
     * @return bookings with user integrations and workspace utilities
     * @throws MissingIdException if user or workspace doesn't have an id
     * @author Daniil Zavyalov
     */
    private fun findIntegrationsAndUtilities(bookingList: List<Booking>): List<Booking> {
        val userIds = mutableSetOf<UUID>()
        val workspaceIds = mutableSetOf<UUID>()
        for (booking in bookingList) {
            for (participant in booking.participants) {
                participant.id?.let {
                    userIds.add(it)
                }
            }
            booking.owner.id?.let {
                userIds.add(it)
            }
            booking.workspace.id?.let {
                workspaceIds.add(it)
            }
        }
        val utilities = workspaceRepository.findAllUtilitiesByWorkspaceIds(workspaceIds)
        val integrations = usersCache.findAllIntegrationsByUserIds(userIds)
        return addIntegrationsAndUtilities(bookingList, integrations, utilities)
    }

    /**
     * Adds integrations and utilities to users and workspace
     * related with the given booking model
     *
     * @param bookingList bookings for which user integrations and workspace utilities need to be added.
     * @return bookings with user integrations and workspace utilities
     * @throws MissingIdException if user model doesn't have an id
     * @author Daniil Zavyalov
     */
    private fun addIntegrationsAndUtilities(
        bookingList: List<Booking>,
        integrations: HashMap<UUID, MutableSet<IntegrationModel>>,
        utilities: HashMap<UUID, MutableList<Utility>>
    ): List<Booking> {
        for (booking in bookingList) {
            booking.workspace.utilities = utilities[booking.workspace.id] ?: listOf()
            booking.owner.integrations = integrations[booking.owner.id] ?: setOf()
            for (participant in booking.participants) {
                participant.integrations = integrations[participant.id]
            }
        }
        return bookingList
    }

    /**
     * Retrieves all integrations for a given user model. Uses [UserModel.id] to find integrations
     *
     * @param user for which integrations need to be added
     * @return List of [IntegrationModel] for the given [UserModel]
     * @throws MissingIdException if user model doesn't have an id
     * @author Daniil Zavyalov
     */
    private fun findIntegrations(user: UserModel): Set<IntegrationModel> {
        val userId = user.id ?: throw MissingIdException("User with name ${user.fullName} doesn't have an id")
        return usersCache.findSetOfIntegrationsByUser(userId)
    }

    /**
     * Retrieves all utilities for a given workspace model. Uses [Workspace.id] to find utilities
     *
     * @param workspace for which utilities need to be added
     * @return List of [Utility] for the given [Workspace]
     * @throws MissingIdException if workspace doesn't have an id
     * @author Daniil Zavyalov
     */
    private fun findUtilities(workspace: Workspace): List<Utility> {
        val workspaceId =
            workspace.id ?: return emptyList() //TODO: return MissingIdException
        return workspaceRepository.findUtilitiesByWorkspaceId(workspaceId)
    }

    /**
     * Saves a given booking. Use the returned model for further operations
     *
     * @param booking [Booking] to be saved
     * @return saved [Booking]
     * @throws InstanceNotFoundException if workspace with the given id not found
     * @author Daniil Zavyalov
     */
    override fun save(booking: Booking): Booking {
        val workspaceId = booking.workspace.id ?: throw MissingIdException("Missing booking workspace id")
        val workspace = workspaceRepository.findById(workspaceId)
            ?: throw InstanceNotFoundException(WorkspaceBookingEntity::class, "Workspace with id $workspaceId not wound")
        return if (workspace.tag == "meeting") {
            logger.error("Saving meeting room booking")
            bookingRepository.save(booking)
        } else {
            logger.error("Saving workspace booking")
            bookingWorkspaceRepository.save(booking)
        }
    }

    /**
     * Updates a given booking. Use the returned model for further operations
     *
     * @param booking changed booking
     * @return [Booking] after change saving
     * @throws InstanceNotFoundException if workspace with the given id not found
     * @author Daniil Zavyalov
     */
    override fun update(booking: Booking): Booking {
        val workspaceId = booking.workspace.id ?: throw MissingIdException("Missing booking workspace id")
        val workspace = workspaceRepository.findById(workspaceId)
            ?: throw InstanceNotFoundException(WorkspaceBookingEntity::class, "Workspace with id $workspaceId not wound")
        return if (workspace.tag == "meeting") {
            logger.error("Updating meeting room booking")
            bookingRepository.update(booking)
        } else {
            logger.error("Updating workspace booking")
            bookingWorkspaceRepository.update(booking)
        }
    }
}
