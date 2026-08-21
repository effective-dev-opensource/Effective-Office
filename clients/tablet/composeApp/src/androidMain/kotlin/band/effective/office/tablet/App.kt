package band.effective.office.tablet

import android.app.Application
import band.effective.office.tablet.core.domain.model.SettingsManager
import band.effective.office.tablet.core.domain.model.SettingsStore
import band.effective.office.tablet.di.KoinInitializer
import com.google.firebase.messaging.FirebaseMessaging
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named

class  App : Application() {

    override fun onCreate() {
        super.onCreate()
        LoggerInitializer().init()
        KoinInitializer().init { androidContext(this@App) }
        val settings = SharedPreferencesSettings(getSharedPreferences("settings", MODE_PRIVATE))
        SettingsManager.init(object : SettingsStore {
            override fun getString(key: String, defaultValue: String) = settings.getString(key, defaultValue)
            override fun putString(key: String, value: String) = settings.putString(key, value)
            override fun remove(key: String) = settings.remove(key)
        })
        subscribeOnFirebaseTopics()
    }

    private fun subscribeOnFirebaseTopics() {
        val topicNameList: List<String> = get(qualifier = named("FireBaseTopics"))
        topicNameList.forEach { topic ->
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
        }
    }
}