package band.effective.office.smsrouter.data.di

import band.effective.office.shared.core.network.HttpClientProvider
import band.effective.office.smsrouter.data.SmsApiService
import band.effective.office.smsrouter.data.SmsApiServiceImpl
import band.effective.office.smsrouter.data.database.AppDatabase
import band.effective.office.smsrouter.data.mapper.SmsDataDtoMapper
import band.effective.office.smsrouter.data.provider.SimCardProviderImpl
import band.effective.office.smsrouter.data.repository.SettingsRepositoryImpl
import band.effective.office.smsrouter.data.repository.SmsForwardingRepositoryImpl
import band.effective.office.smsrouter.data.repository.SmsLogsRepositoryImpl
import band.effective.office.smsrouter.domain.provider.SimCardProvider
import band.effective.office.smsrouter.domain.repository.SettingsRepository
import band.effective.office.smsrouter.domain.repository.SmsForwardingRepository
import band.effective.office.smsrouter.domain.repository.SmsLogsRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { HttpClientProvider.create() }
    single { SmsDataDtoMapper() }
    single<SmsApiService> { SmsApiServiceImpl(client = get()) }
    single<SmsForwardingRepository> { SmsForwardingRepositoryImpl(smsApiService = get(), smsDataDtoMapper = get()) }
    single<SettingsRepository> { SettingsRepositoryImpl(androidContext()) }

    // Database
    single { AppDatabase.getInstance(androidContext()) }
    single { get<AppDatabase>().smsLogDao() }

    // Repository with database support
    single<SmsLogsRepository> { SmsLogsRepositoryImpl(smsLogDao = get()) }

    single<SimCardProvider> {
        SimCardProviderImpl(
            context = androidContext(),
            subscriptionManager = get()
        )
    }
}
