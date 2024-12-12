package band.effective.foosball.network

object UserRepository {

    suspend fun fetchUserNames(tag: String? = null, email: String? = null): List<String> {
        val users = RetrofitInstance.api.getUsers(tag, email)
        return users.map { it.fullName }
    }
}