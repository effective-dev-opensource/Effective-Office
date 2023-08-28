package band.effective.office.elevator.data.repository

import band.effective.office.elevator.domain.models.BookingInfo
import band.effective.office.elevator.domain.models.BookingPeriod
import band.effective.office.elevator.domain.models.CreatingBookModel
import band.effective.office.elevator.domain.models.ErrorWithData
import band.effective.office.elevator.domain.models.TypeEndPeriodBooking
import band.effective.office.elevator.domain.models.User
import band.effective.office.elevator.domain.models.emptyUserDTO
import band.effective.office.elevator.domain.models.emptyWorkSpaceDTO
import band.effective.office.elevator.domain.models.toDTO
import band.effective.office.elevator.domain.models.toDTOModel
import band.effective.office.elevator.domain.models.toDomainZone
import band.effective.office.elevator.domain.repository.BookingRepository
import band.effective.office.elevator.domain.repository.ProfileRepository
import band.effective.office.elevator.ui.employee.aboutEmployee.models.BookingsFilter
import band.effective.office.elevator.utils.localDateTimeToUnix
import band.effective.office.elevator.utils.map
import band.effective.office.network.api.Api
import band.effective.office.network.dto.BookingDTO
import band.effective.office.network.dto.RecurrenceDTO
import band.effective.office.network.model.Either
import band.effective.office.network.model.ErrorResponse
import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.atTime

class BookingRepositoryImpl(
    private val api: Api,
    private val profileRepository: ProfileRepository
) : BookingRepository {

    private var user: User? = null
    init {
        CoroutineScope(Dispatchers.IO).launch {
            val currentUserResponse = profileRepository.getUser()

            currentUserResponse.collect { response ->
                user = when (response) {
                    is Either.Error -> null
                    is Either.Success -> response.data
                }
            }
        }
    }

    private val lastResponse =
        MutableStateFlow<Either<ErrorWithData<List<BookingInfo>>, List<BookingInfo>>>(
            Either.Error(
                ErrorWithData<List<BookingInfo>>(
                    error = ErrorResponse(code = 0, description = ""),
                    saveData = null
                )
            )
        )

    override suspend fun changeBooking(
        bookingInfo: BookingInfo,
        bookingPeriod: BookingPeriod?,
        typeEndPeriod: TypeEndPeriodBooking?
    ) {
        val recurrence = getRecurrenceModal(
            bookingPeriod = bookingPeriod,
            typeEndPeriod = typeEndPeriod
        )

        val bookingDTO = bookingInfo.toDTOModel(
            userDTO = emptyUserDTO(
                id = bookingInfo.ownerId,
                email = user?.email?:"",
            ),
            workspaceDTO = emptyWorkSpaceDTO(bookingInfo.workSpaceId),
            recurrence = recurrence
        )
        api.updateBooking(bookingInfo = bookingDTO)
    }

    override suspend fun deleteBooking(bookingInfo: BookingInfo) {

    }


    override suspend fun deleteBooking(book: String) {
        api.deleteBooking(book)
    }

    override suspend fun createBook(creatingBookModel: CreatingBookModel) {

        val currentUserResponse = profileRepository.getUser()

        currentUserResponse.collect { response ->
            when (response) {
                is Either.Success -> {

                    user = response.data

                    val recurrence = getRecurrenceModal(
                        bookingPeriod = creatingBookModel.bookingPeriod,
                        typeEndPeriod = creatingBookModel.typeOfEndPeriod
                    )
                    Napier.d {
                        "Creaing booking is: $recurrence"
                    }
                    val bookingDTO = creatingBookModel.toDTO(
                        user = emptyUserDTO(
                            id = response.data.id,
                            email = response.data.email
                        ),
                        workspaceDTO = emptyWorkSpaceDTO(creatingBookModel.workSpaceId),
                        recurrence = recurrence
                    )
                    when(val creating= api.createBooking(bookingDTO)){
                        is Either.Error -> {
                            println("Error create booking: ${creating.error}" +
                                    "BooKingDTO: $bookingDTO")
                        }
                        is Either.Success -> println("Creating ok")
                    }
                }

                is Either.Error -> {
                    // TODO add implemetation for error
                }
            }
        }
    }

    override suspend fun getBookingsForUser(
        ownerId: String,
        bookingsFilter: BookingsFilter
    ): Flow<Either<ErrorWithData<List<BookingInfo>>, List<BookingInfo>>> = flow {
        val response = api.getBookingsByUser(ownerId)
            .convert(oldValue = lastResponse.value, filter = bookingsFilter)
        lastResponse.update { response }
        emit(response)
    }


    override suspend fun getBookingsByDate(
        ownerId: String?,
        date: LocalDate,
        bookingsFilter: BookingsFilter
    ): Flow<Either<ErrorWithData<List<BookingInfo>>, List<BookingInfo>>> = flow  {
        if (ownerId == null) {
            val currentUserResponse = profileRepository.getUser()
            currentUserResponse.collect { userResponse ->
                when (userResponse) {
                    is Either.Success -> {
                        val bookings = api.getBookingsByUser(userResponse.data.id)
                            .convertWithDateFilter(
                                oldValue = lastResponse.value,
                                filter = bookingsFilter,
                                dateFilter = date
                            )
                        lastResponse.update { bookings }
                        emit(bookings)
                    }
                    is Either.Error -> {
                        // TODO add implemetation for error
                    }
                }
            }
        }
        else {
            val bookings = api.getBookingsByUser(ownerId)
                .convertWithDateFilter(
                    oldValue = lastResponse.value,
                    filter = bookingsFilter,
                    dateFilter = date
                )
            lastResponse.update { bookings }
            emit(bookings)
        }
    }

    private fun Either<ErrorResponse, List<BookingDTO>>.convert(
        filter: BookingsFilter,
        oldValue: Either<ErrorWithData<List<BookingInfo>>,
                List<BookingInfo>>
    ) =
        map(errorMapper = { error ->
            ErrorWithData(
                error = error, saveData = when (oldValue) {
                    is Either.Error -> oldValue.error.saveData
                    is Either.Success -> oldValue.data
                }
            )
        },
            successMapper = { bookingDTOS ->
                bookingDTOS.filter {booking ->
                    when {
                        filter.workPlace && filter.meetRoom -> true
                        filter.workPlace -> booking.workspace.tag == "regular"
                        filter.meetRoom -> booking.workspace.tag == "meeting"
                        else -> {false}
                    }
                }
                    .toDomainZone() // TODO add filtration by workSpace or meetingRoom
            }
        )

    private fun Either<ErrorResponse, List<BookingDTO>>.convertWithDateFilter(
        filter: BookingsFilter,
        oldValue: Either<ErrorWithData<List<BookingInfo>>,List<BookingInfo>>,
        dateFilter: LocalDate
    ) =
        map(errorMapper = { error ->
            println("errors sow booking $error")
            ErrorWithData(
                error = error, saveData = when (oldValue) {
                    is Either.Error -> oldValue.error.saveData
                    is Either.Success -> oldValue.data
                }
            )
        },
            successMapper = { bookingDTOS ->
                println("bookings: $bookingDTOS")
                bookingDTOS
                    .toDomainZone()
                    .filter {
                        println("repository: ${it.dateOfStart.date} == ${dateFilter}")
                    it.dateOfStart.date == dateFilter
                } // TODO add filtration by workSpace or meetingRoom
            }
        )
    private fun getRecurrenceModal(
        bookingPeriod: BookingPeriod?,
        typeEndPeriod: TypeEndPeriodBooking?
    ): RecurrenceDTO? {
        return if (bookingPeriod is BookingPeriod.NoPeriod || bookingPeriod == null || typeEndPeriod == null) null
        else RecurrenceDTO(
            interval = bookingPeriod.durationPeriod,
            freq = when (bookingPeriod) {
                is BookingPeriod.Month -> "MONTHLY"
                is BookingPeriod.Week -> "WEEKLY"
                is BookingPeriod.Year -> "YEARLY"
                is BookingPeriod.EveryWorkDay -> "WEEKLY"
                else -> "DAILY"
            },
            count = when (typeEndPeriod) {
                is TypeEndPeriodBooking.CountRepeat -> typeEndPeriod.count
                else -> null
            },
            until = when (typeEndPeriod) {
                is TypeEndPeriodBooking.DatePeriodEnd ->
                    localDateTimeToUnix(typeEndPeriod.date.atTime(hour = 0, minute = 0))
                else -> null
            },
            byDay = when (bookingPeriod) {
                is BookingPeriod.Week -> bookingPeriod.selectedDayOfWeek.map { it.dayOfWeekNumber }
                is BookingPeriod.EveryWorkDay -> {
                    listOf(1, 2, 3, 4, 5)
                }
                else -> listOf()
            }
        )
    }
}