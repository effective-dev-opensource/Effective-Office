package band.effective.foosball.network

import retrofit2.http.GET
import retrofit2.http.Query

interface UserService {
    @GET("/api/v1/users")
    suspend fun getUsers(
        @Query("user_tag") tag: String? = null,
        @Query("email") email: String? = null
    ): List<UserResponse>
}
