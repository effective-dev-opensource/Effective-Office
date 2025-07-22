package band.effective.office.smsrouter.data.di

import band.effective.office.base.data.network.HttpClientProvider
import band.effective.office.smsrouter.data.SmsApiService
import band.effective.office.smsrouter.data.SmsApiServiceImpl
import band.effective.office.smsrouter.data.mapper.SmsDataDtoMapper
import band.effective.office.smsrouter.data.repository.SmsForwardingRepositoryImpl
import band.effective.office.smsrouter.domain.repository.SmsForwardingRepository
import io.ktor.http.HttpHeaders
import org.koin.dsl.module

val dataModule = module {
    single {
        HttpClientProvider.create {
            headers.append(
                HttpHeaders.Authorization,
                "Bearer ${"someValue" /*TODO(radchenko): get from config(or env)*/}"
            )
            url("https://21a73fb2dd45.ngrok-free.app") // TODO(radchenko): get from config
        }
    }

    single { SmsDataDtoMapper() }

    single<SmsApiService> { SmsApiServiceImpl(client = get()) }

    single<SmsForwardingRepository> { SmsForwardingRepositoryImpl(smsApiService = get(), smsDataDtoMapper = get()) }
}