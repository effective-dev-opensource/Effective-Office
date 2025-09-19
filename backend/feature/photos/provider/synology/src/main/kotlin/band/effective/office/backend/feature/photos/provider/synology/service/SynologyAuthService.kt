package band.effective.office.backend.feature.photos.provider.synology.service

import band.effective.office.backend.feature.photos.core.exception.PhotoProviderUnavailableException
import band.effective.office.backend.feature.photos.provider.synology.api.SynologyApi
import band.effective.office.backend.feature.photos.provider.synology.config.SynologyCredentials
import band.effective.office.backend.feature.photos.provider.synology.constants.SynologyApiConstants
import band.effective.office.backend.feature.photos.provider.synology.model.SynologyAuthModel
import org.springframework.stereotype.Service

@Service
class SynologyAuthService(
    private val synologyApi: SynologyApi,
    private val credentials: SynologyCredentials
) {
    fun authenticate(): SynologyAuthModel {
        val response = runCatching {
            synologyApi.auth(
                api = SynologyApiConstants.AUTH_API,
                version = SynologyApiConstants.AUTH_VERSION,
                method = SynologyApiConstants.AUTH_METHOD,
                account = credentials.login,
                passwd = credentials.password,
                session = SynologyApiConstants.AUTH_SESSION,
                format = SynologyApiConstants.AUTH_FORMAT
            )
        }.getOrElse { throw PhotoProviderUnavailableException("Synology auth failed: ${it.message}") }

        if (!response.success || response.data?.sid == null) {
            throw PhotoProviderUnavailableException("Synology auth failed: success=${response.success}, sidNull=${response.data?.sid == null}")
        }

        return SynologyAuthModel(sid = response.data.sid)
    }
}