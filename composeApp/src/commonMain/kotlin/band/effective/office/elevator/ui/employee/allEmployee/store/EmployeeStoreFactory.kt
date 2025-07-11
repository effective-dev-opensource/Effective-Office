package band.effective.office.elevator.ui.employee.allEmployee.store


import band.effective.office.elevator.MainRes
import band.effective.office.elevator.domain.models.EmployeeInfo
import band.effective.office.elevator.domain.models.User
import band.effective.office.elevator.domain.useCase.EmployeeUseCase
import band.effective.office.elevator.domain.useCase.GetUserUseCase
import band.effective.office.elevator.ui.employee.allEmployee.models.mappers.toUI
import band.effective.office.elevator.utils.changeEmployeeShowedList
import band.effective.office.network.model.Either
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlinx.coroutines.flow.firstOrNull
internal class EmployeeStoreFactory(private val storeFactory: StoreFactory) : KoinComponent {

    private val employeesInfo: EmployeeUseCase by inject()
    private val userUseCase: GetUserUseCase by inject()


    @OptIn(ExperimentalMviKotlinApi::class)
    fun create(): EmployeeStore =
        object : EmployeeStore,
            Store<EmployeeStore.Intent, EmployeeStore.State, EmployeeStore.Label> by storeFactory.create(
                name = "EmployeeStore",
                initialState = EmployeeStore.State(
                    changeShowedEmployeeCards = emptyList(),
                    countShowedEmployeeCards = "0",
                    query = "",
                    allEmployeeList = listOf()
                ),
                bootstrapper = coroutineBootstrapper {
                    launch {
                        dispatch(Action.UpdateEmployeesInfo)
                    }
                },
                executorFactory = ::ExecutorImpl,
                reducer = ReducerIMPL
            ){}

    private sealed interface Msg {
        data class InitListEmployees(val employeesInfo: List<EmployeeInfo>) : Msg

        data class ChangeQuery(val query: String) : Msg
    }

    private sealed interface Action {
        object UpdateEmployeesInfo : Action
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<EmployeeStore.Intent, Action, EmployeeStore.State, Msg, EmployeeStore.Label>() {
        override fun executeIntent(
            intent: EmployeeStore.Intent,
            getState: () -> EmployeeStore.State
        ) {
            when (intent) {
                is EmployeeStore.Intent.OnTextFieldUpdate -> {
                    scope.launch {
                        dispatch(
                            Msg.ChangeQuery(query = intent.query)
                        )
                    }
                }

                is EmployeeStore.Intent.OnClickOnEmployee -> {
                    scope.launch {
                        publish(
                            EmployeeStore.Label.ShowProfileScreen(
                                employee = getState()
                                    .allEmployeeList
                                    .first { it.id == intent.employeeId }
                            )
                        )
                    }
                }
            }
        }

        override fun executeAction(
            action: Action,
            getState: () -> EmployeeStore.State
        ) {
            when (action) {
                Action.UpdateEmployeesInfo -> {
                    scope.launch(Dispatchers.IO) {
                        var currentUserId: String? = null
                        val userResult = userUseCase.execute().firstOrNull()
                        when (userResult) {
                            is Either.Success -> {
                                currentUserId = userResult.data.id
                            }
                            else -> {}
                        }

                        employeesInfo.invoke().collect { response ->
                            withContext(Dispatchers.Main) {
                                when (response) {
                                    is Either.Success -> {

                                        val filteredEmployees = if (currentUserId != null) {
                                            response.data.filter { it.id != currentUserId }
                                        } else {
                                            response.data
                                        }
                                        dispatch(Msg.InitListEmployees(employeesInfo = filteredEmployees))
                                    }
                                    is Either.Error -> {
                                        publish(EmployeeStore.Label.ShowError(message = MainRes.strings.something_went_wrong))
                                        Napier.e { "error get employees: ${response.error}" }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private object ReducerIMPL : Reducer<EmployeeStore.State, Msg> {
        override fun EmployeeStore.State.reduce(msg: Msg): EmployeeStore.State =
            when (msg) {
                is Msg.InitListEmployees -> {
                    val newEmployeesList =
                        changeEmployeeShowedList(
                            query = query,
                            allEmployeesCards = msg.employeesInfo.map(EmployeeInfo::toUI)
                        )
                    copy(
                        changeShowedEmployeeCards = newEmployeesList,
                        countShowedEmployeeCards = newEmployeesList.count().toString(),
                        allEmployeeList = msg.employeesInfo,
                        isLoading = false
                    )
                }

                is Msg.ChangeQuery -> {
                    val newEmployeesList =
                        changeEmployeeShowedList(
                            query = msg.query,
                            allEmployeesCards = allEmployeeList.map(EmployeeInfo::toUI)
                        )
                    copy(
                        query = msg.query,
                        changeShowedEmployeeCards = newEmployeesList,
                        countShowedEmployeeCards = newEmployeesList.count().toString(),
                        isLoading = false
                    )
                }
            }
    }
}
