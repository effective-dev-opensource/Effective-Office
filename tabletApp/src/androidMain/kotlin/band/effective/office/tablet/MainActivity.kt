package band.effective.office.tablet

import android.app.ActivityOptions
import android.app.admin.DevicePolicyManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import band.effective.office.tablet.domain.model.SharedPref
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.mvikotlin.main.store.DefaultStoreFactory


class MainActivity : AppCompatActivity() {
    companion object{
        var isRunKioskMode = false
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun runKioskMode(){
        val context = this
        val dpm = context.getSystemService(Context.DEVICE_POLICY_SERVICE)
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
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        runKioskMode()
        super.onCreate(savedInstanceState)
        if (BuildConfig.BUILD_TYPE.contentEquals("release")) {
            initializeSentry()
        }
        SharedPref.sharedPref.init(this)
        setContent {
            App(defaultComponentContext(), DefaultStoreFactory())
        }
    }
}