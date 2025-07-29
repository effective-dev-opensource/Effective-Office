package band.effective.office.tablet

import android.app.ActivityOptions
import android.app.admin.DevicePolicyManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import band.effective.office.tablet.core.domain.orchestrator.EventOrchestrator
import band.effective.office.tablet.interaction.TouchEventDispatcher
import band.effective.office.tablet.root.RootComponent
import band.effective.office.tablet.time.TimeReceiver
import com.arkivanov.decompose.defaultComponentContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.milliseconds

class AppActivity : ComponentActivity() {

    companion object{
        var isRunKioskMode = false
    }

    // Create a CoroutineScope for the activity
    private val activityScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    // Create the EventOrchestrator with custom configurations
    private val eventOrchestrator by lazy { 
        EventOrchestrator(activityScope).apply {
            // Configure Firebase events to refresh quickly
            configureTrigger(
                trigger = EventOrchestrator.RefreshTrigger.FIREBASE_EVENT,
                idleTime = 500.milliseconds,
                resetToCurrentDate = false,
                resetToDefaultRoom = false,
                priority = 1
            )

            // Configure inactivity timeout to reset date after 1 minute
            configureTrigger(
                trigger = EventOrchestrator.RefreshTrigger.INACTIVITY_TIMEOUT,
                idleTime = 1.minutes,
                resetToCurrentDate = true,
                resetToDefaultRoom = true,
                priority = 3
            )
        }
    }

    // Create the TouchEventDispatcher
    private val touchEventDispatcher by lazy {
        TouchEventDispatcher(eventOrchestrator)
    }

    // Create the TimeReceiver and initialize it
    private val timeReceiver by lazy {
        TimeReceiver(this)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        runKioskMode()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        timeReceiver.register()

        // Install the touch event dispatcher
        touchEventDispatcher.install(window)

        // Create the root component and pass the EventOrchestrator
        val root = RootComponent(
            componentContext = defaultComponentContext(),
            eventOrchestrator = eventOrchestrator
        )

        setContent { App(root) }
    }

    override fun onDestroy() {
        // Unregister the time receiver
        timeReceiver.unregister()
        super.onDestroy()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun runKioskMode(){
        val context = this
        val dpm = context.getSystemService(DEVICE_POLICY_SERVICE)
                as DevicePolicyManager
        val adminName = AdminReceiver.getComponentName(context)
        val KIOSK_PACKAGE = "band.effective.office.tablet"
        val APP_PACKAGES = arrayOf(KIOSK_PACKAGE)
        if (isRunKioskMode || !dpm.isDeviceOwnerApp(adminName.packageName)) return

        val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
            putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminName)
            putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                "")
        }
        startActivityForResult(intent, 1)

        dpm.setLockTaskPackages(adminName, APP_PACKAGES)

        // Set an option to turn on lock task mode when starting the activity.
        val options = ActivityOptions.makeBasic()
        options.setLockTaskEnabled(true)
        isRunKioskMode = true

        // Start our kiosk app's main activity with our lock task mode option.
        val packageManager = context.packageManager
        val launchIntent = packageManager.getLaunchIntentForPackage(KIOSK_PACKAGE)
        if (launchIntent != null) {
            context.startActivity(launchIntent, options.toBundle())
        }
    }
}
