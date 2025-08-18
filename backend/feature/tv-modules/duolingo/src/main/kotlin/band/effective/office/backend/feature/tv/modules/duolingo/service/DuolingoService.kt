package band.effective.office.backend.feature.tv.modules.duolingo.service

import band.effective.office.backend.feature.tv.modules.duolingo.dto.DuolingoResponseDTO
import band.effective.office.backend.feature.tv.modules.duolingo.dto.DuolingoUserDTO
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException

/**
 * Service for retrieving Duolingo user information.
 */
@Service
class DuolingoService(
    private val webClient: WebClient,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(DuolingoService::class.java)

    /**
     * Retrieves Duolingo user information for the given usernames.
     *
     * @param usernames list of Duolingo usernames
     * @return [DuolingoResponseDTO] containing list of user data
     */
    fun getUsersInfo(usernames: List<String>): DuolingoResponseDTO {
        val results = mutableListOf<DuolingoUserDTO>()

        usernames.forEach { username ->
            try {
                val responseString = webClient.get()
                    .uri { uriBuilder ->
                        uriBuilder.path("/2017-06-30/users")
                            .queryParam("username", username)
                            .build()
                    }
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                    .retrieve()
                    .bodyToMono(String::class.java)
                    .block()

                responseString?.let { responseBody ->
                    val response = objectMapper.readValue(responseBody, DuolingoResponseDTO::class.java)
                    response.users.firstOrNull()?.let { user ->
                        results.add(user)
                    }
                }
            } catch (e: WebClientResponseException) {
                logger.error("Failed to fetch data for username '$username': ${e.message}")
            } catch (e: Exception) {
                logger.error("Unexpected error for username '$username': ${e.message}")
            }
        }

        return DuolingoResponseDTO(users = results)
    }
}