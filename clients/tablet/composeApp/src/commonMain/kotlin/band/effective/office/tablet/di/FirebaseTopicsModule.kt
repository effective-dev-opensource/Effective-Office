package band.effective.office.tablet.di

import org.koin.core.qualifier.named
import org.koin.dsl.module

val firebaseTopicsModule = module {
    single(named("FireBaseTopics")) { listOf("workspace", "user", "booking") }
}