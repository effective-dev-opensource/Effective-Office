package band.effective.office.elevator.ui.profile.mainProfile.store

import band.effective.office.elevator.domain.models.User
import band.effective.office.elevator.domain.useCase.AuthorizationUseCase
import band.effective.office.elevator.domain.useCase.GetUserUseCase
import band.effective.office.elevator.ui.profile.mainProfile.store.ProfileStore.*
import band.effective.office.network.model.Either
import com.arkivanov.mvikotlin.core.store.Reducer
import com.arkivanov.mvikotlin.core.store.Store
import com.arkivanov.mvikotlin.core.store.StoreFactory
import com.arkivanov.mvikotlin.core.utils.ExperimentalMviKotlinApi
import com.arkivanov.mvikotlin.extensions.coroutines.CoroutineExecutor
import com.arkivanov.mvikotlin.extensions.coroutines.coroutineBootstrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class ProfileStoreFactory(
    private val storeFactory: StoreFactory,
) : KoinComponent {

    private val authorizationUseCase: AuthorizationUseCase by inject()
    private val getUserUseCase: GetUserUseCase by inject()

    @OptIn(ExperimentalMviKotlinApi::class)
    fun create(): ProfileStore =
        object : ProfileStore, Store<Intent, State, Label> by storeFactory.create(
            name = "ProfileStore",
            initialState = State(user = User.defaultUser),
            bootstrapper = coroutineBootstrapper {
                dispatch(Action.FetchUserInfo)
            },
            executorFactory = ::ExecutorImpl,
            reducer = ReducerImpl
        ) {}

    private sealed interface Action {
        object FetchUserInfo : Action
    }

    private sealed interface Msg {
        data class ProfileData(val user: User) : Msg
    }

    private inner class ExecutorImpl :
        CoroutineExecutor<Intent, Action, State, Msg, Label>() {
        override fun executeIntent(intent: Intent, getState: () -> State) {
            when (intent) {
                is Intent.SignOutClicked -> doSignOut()
            }
        }


        private fun doSignOut() {
            authorizationUseCase.logout()
            publish(Label.OnSignedOut)
        }

        override fun executeAction(action: Action, getState: () -> State) {
            when (action) {
                Action.FetchUserInfo -> fetchUserInfo()
            }
        }

        private fun fetchUserInfo() {
            scope.launch(Dispatchers.IO) {
                getUserUseCase
                getUserUseCase.execute().collect { user ->
                    withContext(Dispatchers.Main) {
                        when (user) {
                            is Either.Success -> {
                                dispatch(Msg.ProfileData(user = user.data))
                            }
                            is Either.Error -> {
                                // TODO show error on UI
                                user.error.saveData?.let {
                                    dispatch(Msg.ProfileData(user = it))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private object ReducerImpl : Reducer<State, Msg> {
        override fun State.reduce(message: Msg): State =
            when (message) {
                is Msg.ProfileData -> copy(user = message.user,
                    isLoading = false)
            }
    }
}
