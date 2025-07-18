package band.effective.office.tablet

import android.app.Application
import android.util.Log
import band.effective.office.tablet.core.domain.model.SettingsManager
import band.effective.office.tablet.di.KoinInitializer
import com.google.firebase.messaging.FirebaseMessaging
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.android.get
import org.koin.core.qualifier.named

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        LoggerInitializer().init()
        KoinInitializer().init()
        SettingsManager.init(
            SharedPreferencesSettings(
                this.getSharedPreferences(
                    "settings",
                    MODE_PRIVATE
                )
            )
        )
        subscribeOnFirebaseTopics()
    }

    private fun subscribeOnFirebaseTopics() {
        val topicNameList: List<String> = get(qualifier = named("FireBaseTopics"))
        topicNameList.forEach { topic ->
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
        }
    }
}