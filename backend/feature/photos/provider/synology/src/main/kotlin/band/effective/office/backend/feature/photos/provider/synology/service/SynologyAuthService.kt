package band.effective.office.backend.feature.photos.provider.synology.service

import band.effective.office.backend.feature.photos.core.exception.PhotoProviderUnavailableException
import band.effective.office.backend.feature.photos.provider.synology.api.SynologyApi
import band.effective.office.backend.feature.photos.provider.synology.config.SynologyCredentials
import band.effective.office.backend.feature.photos.provider.synology.constants.SynologyApiConstants
import band.effective.office.backend.feature.photos.provider.synology.model.SynologyAuthModel
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class SynologyAuthService(
    private val synologyApi: SynologyApi,
    private val credentials: SynologyCredentials
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

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

        logger.debug("Successfully authenticated with Synology, SID: ${response.data.sid.take(8)}...")
        return SynologyAuthModel(sid = response.data.sid)
    }

    /**
     * Validates existing session via SYNO.API.Auth method "info"
     * @param sid Session ID to validate
     * @return true if session is valid, false if expired or invalid
     */
    fun isSessionValid(sid: String): Boolean {
        return runCatching {
            val response = synologyApi.checkSession(
                api = SynologyApiConstants.AUTH_API,
                version = SynologyApiConstants.AUTH_VERSION,
                method = SynologyApiConstants.AUTH_INFO_METHOD,
                session = SynologyApiConstants.AUTH_SESSION,
                sid = sid
            )
            
            val isValid = response.success
            logger.debug("Session validation result: $isValid (SID: ${sid.take(8)}...)")
            isValid
        }.getOrElse { 
            logger.warn("Session validation failed with exception: ${it.message}")
            false 
        }
    }
}