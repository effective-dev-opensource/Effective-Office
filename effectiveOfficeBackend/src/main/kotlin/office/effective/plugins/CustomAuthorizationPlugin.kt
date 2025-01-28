package office.effective.plugins


import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import office.effective.features.auth.service.AuthorizationPipeline
import org.koin.core.context.GlobalContext
import org.slf4j.LoggerFactory

/**
 * Allows to check Authentication plugins automatically.
 * Run every time when receiving input call. Checks Authentication (bearer) header containment
 * */
val CustomAuthorizationPlugin = createApplicationPlugin(name = "CustomAuthorizationPlugin") {
    val pluginOn: Boolean = ((System.getenv("VERIFICATION_PLUGIN_ENABLE"))?:"").equals("true") //
    val logger = LoggerFactory.getLogger(this::class.java)
    logger.info("Authorization plugin mode enabled?: $pluginOn")
    logger.info("Authorization plugin installed")
    val authenticationPipeline: AuthorizationPipeline = GlobalContext.get().get()

    onCall { call ->
        run {
            if (pluginOn) {
                if (authenticationPipeline.authorize(call)) {
                    logger.debug("Authorization succeed")
                } else {
                    logger.info("Authorization failed")
                    call.response.status(HttpStatusCode.Unauthorized)
                    call.respond("401: authorization failed")
                }

            }
        }
    }
}
