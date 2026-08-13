package band.effective.office.tablet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.lifecycleScope
import band.effective.office.tablet.root.RootComponent
import band.effective.office.tablet.utils.KioskCommandBus
import band.effective.office.tablet.utils.KioskLifecycleObserver
import band.effective.office.tablet.utils.KioskManager
import com.arkivanov.decompose.defaultComponentContext

val LocalKioskManager = staticCompositionLocalOf<KioskManager?> { null }

class AppActivity : ComponentActivity() {

    private val kioskManager by lazy { KioskManager(this) }
    private val kioskCommandBus = KioskCommandBus.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        lifecycle.addObserver(KioskLifecycleObserver(this, kioskManager, kioskCommandBus, lifecycleScope))

        val root = RootComponent(componentContext = defaultComponentContext())

        setContent {
            CompositionLocalProvider(LocalKioskManager provides kioskManager) {
                App(root)
            }
        }
    }
}