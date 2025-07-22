package band.effective.office.smsrouter.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Routes {

    @Serializable
    object Messages : Routes()

    @Serializable
    object Settings : Routes()
}