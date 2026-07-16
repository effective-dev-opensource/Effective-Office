package band.effective.office.tablet

import android.app.Application
import band.effective.office.tablet.core.domain.manager.DateResetManager
import band.effective.office.tablet.core.domain.model.SettingsManager
import band.effective.office.tablet.core.domain.model.SettingsStore
import band.effective.office.tablet.core.ui.inactivity.InactivityLifecycleCallbacks
import band.effective.office.tablet.di.KoinInitializer
import band.effective.office.tablet.di.firebaseTopicsModule
import com.google.firebase.messaging.FirebaseMessaging
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.android.get
import org.koin.core.qualifier.named
import kotlin.time.Duration.Companion.minutes

class  App : Application() {

    override fun onCreate() {
        super.onCreate()
        LoggerInitializer().init()
        KoinInitializer().init(listOf(firebaseTopicsModule))
        val settings = SharedPreferencesSettings(getSharedPreferences("settings", MODE_PRIVATE))
        SettingsManager.init(object : SettingsStore {
            override fun getString(key: String, defaultValue: String) = settings.getString(key, defaultValue)
            override fun putString(key: String, value: String) = settings.putString(key, value)
            override fun remove(key: String) = settings.remove(key)
        })
        subscribeOnFirebaseTopics()
        initializeInactivitySystem()
    }

    private fun subscribeOnFirebaseTopics() {
        val topicNameList: List<String> = get(qualifier = named("FireBaseTopics"))
        topicNameList.forEach { topic ->
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
        }
    }

    private fun initializeInactivitySystem() {
        InactivityLifecycleCallbacks.initialize(
            application = this,
            timeoutMs = 1.minutes.inWholeMilliseconds,
            callback = {
                DateResetManager.resetDate()
            }
        )
    }
}