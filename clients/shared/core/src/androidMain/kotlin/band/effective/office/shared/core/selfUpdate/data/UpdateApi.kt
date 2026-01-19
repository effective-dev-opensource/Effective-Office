package band.effective.office.shared.core.selfUpdate.data

import band.effective.office.shared.core.selfUpdate.domain.UpdateInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class UpdateApi(
    private val client: HttpClient,
    private val apiUrl: String
) {
    suspend fun getUpdateInfo() = runCatching {
        client.config {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }.get(apiUrl).body<UpdateInfo>()
    }
}