package band.effective.office.tablet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.lifecycleScope
import band.effective.office.tablet.time.TimeReceiver
import band.effective.office.tablet.utils.KioskCommandBus
import band.effective.office.tablet.utils.KioskLifecycleObserver
import band.effective.office.tablet.utils.KioskManager

val LocalKioskManager = staticCompositionLocalOf<KioskManager?> { null }

class AppActivity : ComponentActivity() {

    private val timeReceiver by lazy { TimeReceiver(this) }

    private val kioskManager by lazy { KioskManager(this) }
    private val kioskCommandBus = KioskCommandBus.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        timeReceiver.register()
        enableEdgeToEdge()

        lifecycle.addObserver(KioskLifecycleObserver(this, kioskManager, kioskCommandBus, lifecycleScope))

        setContent {
            CompositionLocalProvider(LocalKioskManager provides kioskManager) {
                AppRoot()
            }
        }
    }

    override fun onDestroy() {
        timeReceiver.unregister()
        super.onDestroy()
    }
}