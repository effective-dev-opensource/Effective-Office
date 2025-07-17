package band.effective.office.tablet.di

import org.koin.core.qualifier.named
import org.koin.dsl.module

val firebaseTopicsModule = module {
    single(named("FireBaseTopics")) { listOf("effectiveoffice-workspace", "effectiveoffice-user", "effectiveoffice-booking") }
}