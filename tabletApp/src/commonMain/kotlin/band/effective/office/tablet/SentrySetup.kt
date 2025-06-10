package band.effective.office.tablet

import io.sentry.kotlin.multiplatform.Sentry

fun initializeSentry() {
    Sentry.init { options ->
        options.dsn = BuildConfig.sentryTabletDsnUrl

        options.experimental.sessionReplay.onErrorSampleRate = 1.0
        options.experimental.sessionReplay.sessionSampleRate = 1.0
    }
}
