package band.effective.office.smsrouter.domain.di

import band.effective.office.smsrouter.domain.usecase.ForwardSmsUseCase
import band.effective.office.smsrouter.domain.usecase.ForwardSmsUseCaseImpl
import org.koin.dsl.module

val domainModule = module {
    single<ForwardSmsUseCase> {
        ForwardSmsUseCaseImpl(
            get(),
            get(),
            get(),
        )
    }
}