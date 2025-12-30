package band.effective.office.backend.feature.photo.saver.provider.mattermost.api

import band.effective.office.backend.feature.photo.saver.provider.mattermost.dto.MattermostChannelDTO
import band.effective.office.backend.feature.photo.saver.provider.mattermost.dto.MattermostPostsResponseDTO
import band.effective.office.backend.feature.photo.saver.provider.mattermost.dto.MattermostUserInfoDTO
import band.effective.office.backend.feature.photo.saver.provider.mattermost.dto.ReactionRequestDTO
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange
import org.springframework.web.service.annotation.HttpExchange
import org.springframework.web.service.annotation.PostExchange
import reactor.core.publisher.Flux

/**
 * Mattermost API interface using Spring Web Service.
 */
@HttpExchange
interface MattermostApi {

    @GetExchange("/api/v4/users/me/channels")
    fun getChannels(): List<MattermostChannelDTO>

    @GetExchange("/api/v4/channels/{channelId}/posts")
    fun getPostsFromChannel(
        @PathVariable channelId: String,
        @RequestParam since: Long
    ): MattermostPostsResponseDTO

    @GetExchange("/api/v4/files/{fileId}")
    fun downloadFile(
        @PathVariable fileId: String
    ): Flux<DataBuffer>

    @PostExchange("/api/v4/reactions")
    fun makeReaction(
        @RequestBody reaction: ReactionRequestDTO
    ): ReactionRequestDTO

    @GetExchange("/api/v4/users/me")
    fun getUserInfo(): MattermostUserInfoDTO
}
