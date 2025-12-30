package band.effective.office.smsrouter.presentation.di

import android.content.Context
import android.telephony.SubscriptionManager
import band.effective.office.smsrouter.presentation.screens.messages.MessageScreenViewModel
import band.effective.office.smsrouter.presentation.screens.settings.SettingsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    // Provide SubscriptionManager
    single {
        androidContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    }

    // Provide ViewModels
    viewModelOf(::SettingsViewModel)
    viewModelOf(::MessageScreenViewModel)
}
