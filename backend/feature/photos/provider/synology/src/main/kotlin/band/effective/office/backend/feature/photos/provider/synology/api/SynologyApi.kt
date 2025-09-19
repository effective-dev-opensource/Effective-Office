package band.effective.office.backend.feature.photos.provider.synology.api

import band.effective.office.backend.feature.photos.provider.synology.dto.SynologyAlbumsResponseDTO
import band.effective.office.backend.feature.photos.provider.synology.dto.SynologyApiResponseDTO
import band.effective.office.backend.feature.photos.provider.synology.dto.SynologyAuthResponseDTO
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.service.annotation.GetExchange

interface SynologyApi {
    @GetExchange("/webapi/auth.cgi")
    fun auth(
        @RequestParam("api") api: String,
        @RequestParam("version") version: Int,
        @RequestParam("method") method: String,
        @RequestParam("account") account: String,
        @RequestParam("passwd") passwd: String,
        @RequestParam("session") session: String,
        @RequestParam("format") format: String
    ): SynologyAuthResponseDTO

    @GetExchange("/webapi/entry.cgi")
    fun albums(
        @RequestParam("api") api: String,
        @RequestParam("version") version: Int,
        @RequestParam("method") method: String,
        @RequestParam("offset") offset: Int,
        @RequestParam("limit") limit: Int,
        @RequestParam("_sid") sid: String
    ): SynologyAlbumsResponseDTO

    @GetExchange("/webapi/entry.cgi")
    fun photos(
        @RequestParam("api") api: String,
        @RequestParam("version") version: Int,
        @RequestParam("method") method: String,
        @RequestParam("offset") offset: Int,
        @RequestParam("limit") limit: Int,
        @RequestParam("album_id") albumId: Int,
        @RequestParam("additional") additional: String,
        @RequestParam("_sid") sid: String
    ): SynologyApiResponseDTO
}