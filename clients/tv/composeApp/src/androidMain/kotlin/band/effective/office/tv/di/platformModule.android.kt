package band.effective.office.tv.di

import band.effective.office.shared.core.crashlytics.OfficeUncaughtExceptionHandler
import band.effective.office.shared.core.crashlytics.SendCrashApi
import band.effective.office.shared.core.network.HttpClientFactory
import band.effective.office.shared.core.selfUpdate.data.UpdateApi
import band.effective.office.shared.core.selfUpdate.data.UpdateInfoRepositoryImpl
import band.effective.office.shared.core.selfUpdate.domain.FileDownloader
import band.effective.office.shared.core.selfUpdate.domain.SelfUpdateInteractor
import band.effective.office.shared.core.selfUpdate.domain.UpdateInfoRepository
import band.effective.office.shared.core.selfUpdate.domain.UpdateInstaller
import band.effective.office.shared.core.selfUpdate.platform.ApkInstaller
import band.effective.office.shared.core.selfUpdate.platform.FileDownloaderImpl
import band.effective.office.tv.BuildConfig
import band.effective.office.tv.BuildKonfig
import io.ktor.client.HttpClient
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

private val SEND_CRASH_QUALIFIER = named("SendCrashQualifier")

actual fun platformModule(): Module = module {
    // crashlytics
    single<HttpClient>(SEND_CRASH_QUALIFIER) { HttpClientFactory.createHttpClient() }
    single<SendCrashApi> {
        SendCrashApi(
            botToken = BuildKonfig.CRASH_HANDLER_BOT_TOKEN,
            chatId = BuildKonfig.CRASH_HANDLER_CHAT_ID,
            client = get(SEND_CRASH_QUALIFIER)
        )
    }
    factory<OfficeUncaughtExceptionHandler> {
        OfficeUncaughtExceptionHandler(
            context = get(),
            enabled = !BuildConfig.DEBUG, // enable crashlytics only in prod
            handler = it.get(),
            crashApi = get()
        )
    }

    // self-update
    single<UpdateApi> { UpdateApi(get(SEND_CRASH_QUALIFIER), BuildKonfig.SELF_UPDATE_API_URL) }
    single<UpdateInfoRepository> { UpdateInfoRepositoryImpl(get()) }
    single<UpdateInstaller> { ApkInstaller(get()) }
    single<FileDownloader> { FileDownloaderImpl(get(SEND_CRASH_QUALIFIER)) }
    single<SelfUpdateInteractor> { SelfUpdateInteractor(get(), get(), get()) }
}