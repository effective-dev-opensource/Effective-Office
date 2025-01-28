package office.effective.features.user.di

import office.effective.features.user.converters.IntegrationDTOModelConverter
import office.effective.features.user.converters.IntegrationModelEntityConverter
import office.effective.features.user.converters.UserDTOModelConverter
import office.effective.features.user.converters.UserModelEntityConverter
import office.effective.features.user.facade.UserFacade
import office.effective.features.user.repository.UsersRepository
import office.effective.features.user.repository.cache.UsersCache
import office.effective.serviceapi.IUserService
import office.effective.features.user.service.UserService
import org.koin.dsl.module

val userDIModule = module(createdAtStart = true) {
    single<IUserService> { UserService(get(),get()) }
    single<IntegrationModelEntityConverter> { IntegrationModelEntityConverter() }
    single<UserModelEntityConverter> { UserModelEntityConverter() }
    single<UserDTOModelConverter> { UserDTOModelConverter(get(), get(), get()) }
    single<UsersRepository> { UsersRepository(get(), get(), get()) }
    single<UserFacade> { UserFacade(get(), get(), get()) }
    single<UsersCache> { UsersCache(get()) }
    single<IntegrationDTOModelConverter> { IntegrationDTOModelConverter(get()) }
}