package band.effective.office.elevator.data.database

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import band.effective.office.elevator.Database
import band.effective.office.elevator.domain.models.User
import band.effective.office.elevator.domain.models.toUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DBSourceImpl(
    val database: Database
) : DBSource {

    private val profileQueries = database.profileQueries

    override fun getUserFlow(): Flow<User?> {
        return profileQueries.selectUser()
            .asFlow()
            .mapToOneOrNull(context = Dispatchers.IO)
            .map { profile -> profile?.toUser() }
    }

    override suspend fun getCurrentUserInfo(): User? {
        val profile = profileQueries.selectUser().executeAsOneOrNull()
        return profile?.toUser()
    }

    override suspend fun update(user: User, idToken: String) {
        with(user) {
            profileQueries.updateUser(
                id = id,
                name = userName,
                post = post,
                email = email,
                phoneNumber = phoneNumber,
                telegramNick = telegram,
                imageUrl = imageUrl
            )
        }
        profileQueries.updateIdToken(idToken = idToken)
    }

    override suspend fun update(user: User) {
        with(user) {
            profileQueries.updateUser(
                id = id,
                name = userName,
                post = post,
                email = email,
                phoneNumber = phoneNumber,
                telegramNick = telegram,
                imageUrl = imageUrl
            )
        }
    }

    override fun deleteUserData() {
        profileQueries.deleteUser()
        profileQueries.deleteToken()
    }
}