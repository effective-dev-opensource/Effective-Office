package band.effective.office.tablet

import android.app.Application
import band.effective.office.tablet.di.KoinInitializer
import band.effective.office.tablet.di.firebaseTopicsModule
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named

class  App : Application() {

    override fun onCreate() {
        super.onCreate()
        LoggerInitializer().init()
        KoinInitializer().init {
            androidContext(applicationContext)
            modules(firebaseTopicsModule)
        }
        subscribeOnFirebaseTopics()
    }

    private fun subscribeOnFirebaseTopics() {
        val topicNameList: List<String> = get(qualifier = named("FireBaseTopics"))
        topicNameList.forEach { topic ->
            FirebaseMessaging.getInstance().subscribeToTopic(topic)
        }
    }
}
