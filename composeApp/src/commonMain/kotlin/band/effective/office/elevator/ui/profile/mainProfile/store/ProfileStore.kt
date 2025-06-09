package band.effective.office.elevator.ui.profile.mainProfile.store

import band.effective.office.elevator.domain.models.User
import com.arkivanov.mvikotlin.core.store.Store

interface ProfileStore : Store<ProfileStore.Intent, ProfileStore.State, ProfileStore.Label> {

    sealed interface Intent {
        object SignOutClicked : Intent
        object FetchUserInfo : Intent
    }

    data class State(
        val isLoading: Boolean = true,
        val user: User,
    )

    sealed interface Label {
        object OnSignedOut : Label
    }
}